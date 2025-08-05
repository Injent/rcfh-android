package ru.rcfh.core.sdui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.IndexAwareStateList
import ru.rcfh.core.sdui.common.Row
import ru.rcfh.core.sdui.event.AddPage
import ru.rcfh.core.sdui.event.RemovePage
import ru.rcfh.core.sdui.event.SetPlus
import ru.rcfh.core.sdui.event.SetVariable
import java.util.Locale

data class YarusTotal(
    val values: Map<String, String>,
)

class Table4State(
    override val id: String,
    val name: String,
    private val rowTemp: (Int) -> List<FieldState>,
    private val dependency: String?,
    document: DocumentState,
    initialValue: List<List<FieldState>>,
) : FieldState(document = document), Container {
    private val triggerIds = rowTemp(-1).flatMap { field ->
        when (field) {
            is Container -> field.items().map(FieldState::id)
            else -> listOf(field.id)
        }
    }

    val rows = IndexAwareStateList(initialValue.map {
        Row(delegate = it.toMutableStateList())
    }.toMutableStateList())
    val colCount = rowTemp(-1).sumOf {
        when (it) {
            is RatioState -> it.values.size
            is RepeatableState -> 0
            else -> 1
        }
    }
    var totals by mutableStateOf(emptyList<YarusTotal>())

    init {
        document.observeEvent(AddPage::class) { event ->
            if (event.templateId == dependency) {
                rows.add(Row(rowTemp(rows.size).toMutableStateList()))
            }
        }
        document.observeEvent(RemovePage::class) { event ->
            if (event.templateId == dependency) {
                rows.removeAt(event.rowIndex)
            }
        }
        document.observeEvent(SetPlus::class) { event ->
            if (event.tableId == dependency) {
                rows.getOrNull(event.rowIndex)?.forEach { field ->
                    when (field) {
                        is RatioState -> field.values.forEach { it.enabled = !event.isSet }
                        is TextState -> field.enabled = !event.isSet
                        is RepeatableState -> field.enabled = !event.isSet
                        else -> Unit
                    }
                }
            }
        }
        document.observeEvent(SetVariable::class) { event ->
            if (event.templateId !in triggerIds) return@observeEvent

            val groupedTier = rows
                .map { row ->
                    row.toMap()
                }
                .filter { row -> row["dolap"].let { it != null && it != "+" } && !row["yarus"].isNullOrEmpty() }
                .groupBy { row -> row["yarus"]!!.toInt() }

            val catValue = mapOf(
                "bpodolap" to 1, "osldolap" to 2, "sosldolap" to 3, "usdolap" to 4, "svsuhdolap" to 5,
                "svvtrdolap" to 5, "svburdolap" to 5, "stsuhdolap" to 5, "stvtrdolap" to 5, "stburdolap" to 5
            )
            val categories = listOf("bpodolap", "osldolap", "sosldolap", "usdolap", "svsuhdolap",
                "svvtrdolap", "svburdolap", "stsuhdolap", "stvtrdolap", "stburdolap")

            // 2d массив который содержит построчно посчитанные категории
            val plainCategories = groupedTier.map { (tier, rows) ->
                tier to rows.map { row ->
                    // Умножаем долю на все категории в строке
                    val dola = row["dolap"]?.toDoubleOrNull() ?: 0.0
                    categories.map { categoryId ->
                        val catValue = row[categoryId]?.toDoubleOrNull() ?: 0.0
                        categoryId to dola * catValue
                    }.toMap()
                }
            }.toMap()

            val maxZapas = groupedTier.map { (tier, rows) ->
                tier to rows.maxOf { it["zapaspor"]?.toDoubleOrNull() ?: 0.0 }
            }.toMap()
            val totalZapas = maxZapas.values.sum()

            val results = plainCategories.map { (tier, rows) ->
                tier to categories.map { catId ->
                    catId to (rows.map { row ->
                        row[catId] ?: 0.0
                    }.average() * rows.size) / 10
                }.toMap()
            }.toMap()

            val tieredSks = mutableMapOf<Int, Double>()
            val tot = plainCategories.map { (tier, rows) ->
                val doubleValues = categories.map { catId ->
                    catId to (rows.map { row ->
                        row[catId] ?: 0.0
                    }.average() * rows.size) / 10
                }.toMap()

                val sks = doubleValues.map { (catId, value) ->
                    value * catValue[catId]!!
                }.sum() / 100.0
                tieredSks[tier] = sks

                YarusTotal(
                    values = doubleValues.mapValues { "%.1f".format(Locale.ENGLISH, it.value) }
                    + ("sksporoda" to "%.2f".format(Locale.ENGLISH, sks))
                    + ("zapaspor" to "Итог ярус $tier")
                )
            }.toMutableList()

            val totalSks = tieredSks.map { (tier, sks) ->
                (maxZapas[tier] ?: 0.0) * sks
            }.sum() / totalZapas

            tot += YarusTotal(
                values = categories.map { catId ->
                    val num = results.map { (tier, values) ->
                        (values[catId] ?: 0.0) * (maxZapas[tier] ?: 0.0)
                    }.sum() / totalZapas
                    catId to "%.1f".format(Locale.ENGLISH, num)
                }.toMap()
                    + ("sksporoda" to "%.2f".format(Locale.ENGLISH, totalSks))
                    + ("zapaspor" to "Итог по насаждению")
            )

            totals = tot.toList()
        }
    }

    override fun isValid(): Boolean = true

    override fun save(): JsonElement {
        return buildJsonObject {
            putJsonArray("values") {
                rows.forEach { row ->
                    addJsonObject {
                        row.forEach { state ->
                            put(state.id, state.save())
                        }
                    }
                }
            }
        }
    }

    override fun detectErrors(): List<DetectedError> {
        return emptyList()
    }

    override fun items(): List<FieldState> {
        return rows.flatten()
    }
}

fun FieldState.getName(): String {
    return when (this) {
        is LinkedState -> this.label
        is CalculatedState -> this.label
        is TextState -> this.label
        else -> "NULL"
    }
}

fun Row.getFields(): List<FieldState> {
    return this.flatMap { field ->
        when (field) {
            is RatioState -> field.values
            is RepeatableState -> listOf(field)
            else -> listOf(field)
        }
    }
}