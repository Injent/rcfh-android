package ru.rcfh.core.sdui.template

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.rcfh.core.sdui.common.ComputeMetadata
import ru.rcfh.core.sdui.common.Format
import ru.rcfh.core.sdui.common.Formula
import ru.rcfh.core.sdui.common.RefDependency
import ru.rcfh.core.sdui.common.Rule
import ru.rcfh.core.sdui.common.Visual
import ru.rcfh.core.sdui.state.CalculatedState
import ru.rcfh.core.sdui.state.ComparisonTableState
import ru.rcfh.core.sdui.state.DocumentState
import ru.rcfh.core.sdui.state.FieldState
import ru.rcfh.core.sdui.state.LinkedState
import ru.rcfh.core.sdui.state.LocationState
import ru.rcfh.core.sdui.state.RatioState
import ru.rcfh.core.sdui.state.RepeatableState
import ru.rcfh.core.sdui.state.TablePageState
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.core.sdui.state.TextState

@Serializable
sealed interface Template {
    val id: String

    fun restore(
        documentState: DocumentState,
        json: JsonElement = JsonNull,
        rowIndex: Int = -1,
        param: Any? = null
    ): FieldState

    @Serializable
    @SerialName("text")
    data class Text(
        override val id: String,
        val label: String,
        val visual: Visual,
        val hint: String? = null,
        val format: Format = Format.None,
        val rules: List<Rule> = emptyList()
    ) : Template {
        override fun restore(
            documentState: DocumentState,
            json: JsonElement,
            rowIndex: Int,
            param: Any?
        ): FieldState {
            return TextState(
                id = id,
                label = label,
                hint = hint,
                visual = visual,
                documentState = documentState,
                rules = rules,
                initialValue = runCatching {
                    json.jsonPrimitive.contentOrNull!!
                }.getOrDefault(
                    if (visual is Visual.Checkbox) "false" else ""
                ),
                rowIndex = rowIndex,
                metadata = param as? ComputeMetadata,
                format = format
            ).apply {
                if (visual is Visual.Reference) {
                    runCatching {
                        with(json.jsonObject) {
                            this@apply.setReference(
                                refDependency = RefDependency(
                                    handbookId = get("handbookId")!!.jsonPrimitive.int,
                                    refId = get("refId")!!.jsonPrimitive.int
                                ),
                                value = get("value")!!.jsonPrimitive.content
                            )
                        }
                    }
                }
            }
        }
    }

    @Serializable
    @SerialName("linked")
    data class Linked(
        override val id: String,
        val label: String,
    ) : Template {
        override fun restore(
            documentState: DocumentState,
            json: JsonElement,
            rowIndex: Int,
            param: Any?
        ): FieldState {
            return LinkedState(
                id = id,
                label = label,
                documentState = documentState,
                initialValue = runCatching {
                    json.jsonPrimitive.contentOrNull!!
                }.getOrDefault(""),
                rowIndex = rowIndex
            )
        }
    }

    @Serializable
    @SerialName("ratio")
    data class Ratio(
        override val id: String,
        val templates: List<Text>
    ) : Template {
        override fun restore(
            documentState: DocumentState,
            json: JsonElement,
            rowIndex: Int,
            param: Any?
        ): FieldState {
            return RatioState(
                id = id,
                values = runCatching {
                    templates.map { template ->
                        template.restore(
                            documentState = documentState,
                            json = runCatching {
                                json.jsonObject[template.id]?.jsonPrimitive
                                    ?.takeUnless { it.contentOrNull.isNullOrEmpty() }
                                    ?: JsonPrimitive("0")
                            }.getOrDefault(JsonPrimitive("0")),
                            rowIndex = rowIndex
                        ) as TextState
                    }.toImmutableList()
                }.getOrDefault(persistentListOf()),
                documentState = documentState
            )
        }
    }

    @Serializable
    @SerialName("calculated")
    data class Calculated(
        override val id: String,
        val label: String,
        val formula: Formula,
        val unit: String = "",
    ) : Template {
        override fun restore(
            documentState: DocumentState,
            json: JsonElement,
            rowIndex: Int,
            param: Any?
        ): FieldState {
            return CalculatedState(
                id = id,
                label = label,
                unit = unit,
                formula = formula,
                documentState = documentState,
                initialValue = runCatching {
                    json.jsonPrimitive.contentOrNull!!
                }.getOrDefault(""),
                rowIndex = rowIndex,
                metadata = (param as? ComputeMetadata)
            )
        }
    }

    @Serializable
    @SerialName("table")
    data class Table(
        override val id: String,
        val name: String,
        val columns: List<Template>,
        val dependency: String? = null,
        val total: Map<String, Calculated> = emptyMap(),
        val extraSummary: Calculated? = null
    ) : Template {
        override fun restore(
            documentState: DocumentState,
            json: JsonElement,
            rowIndex: Int,
            param: Any?
        ): FieldState {
            val values = runCatching {
                val rows = json.jsonObject["values"]!!.jsonArray.map { it.jsonObject }

                rows.mapIndexed { rowIndex, rowJson ->
                    columns.map { template ->
                        template.restore(
                            documentState = documentState,
                            json = tryParse { rowJson[template.id]!! },
                            rowIndex = rowIndex,
                            param = ComputeMetadata(inTable = id)
                        )
                    }
                }
            }.getOrDefault(
                emptyList()
            )

            val totalValues = runCatching {
                total.mapValues { (_, template) ->
                    val obj = tryParse { json.jsonObject["total"]!! }
                    template.restore(
                        documentState = documentState,
                        json = tryParse { obj.jsonObject[template.id]!!.jsonPrimitive },
                        param = ComputeMetadata(inTable = id)
                    ) as CalculatedState
                }
            }
                .onFailure { it.printStackTrace() }
                .getOrDefault(emptyMap())

            return TableState(
                id = id,
                name = name,
                emptyTemplate = { index -> createEmptyTemp(documentState, index) },
                initialValue = values,
                dependency = dependency,
                documentState = documentState,
                emptySummary = if (extraSummary != null) {
                    { index ->
                        extraSummary.restore(
                            documentState, JsonNull, index, param
                        ) as CalculatedState
                    }
                } else null,
                total = totalValues.toImmutableMap()
            )
        }

        private fun createEmptyTemp(
            documentState: DocumentState,
            rowIndex: Int
        ): SnapshotStateList<FieldState> {
            return columns.map { column ->
                column.restore(
                    documentState = documentState,
                    rowIndex = rowIndex
                )
            }.toMutableStateList()
        }
    }

    @Serializable
    @SerialName("comparison_table")
    data class ComparisonTable(
        override val id: String,
        val name: String,
        val maxEntries: Int = 10,
        val templates: List<Text>,
    ) : Template {
        override fun restore(
            documentState: DocumentState,
            json: JsonElement,
            rowIndex: Int,
            param: Any?
        ): FieldState {
            val values = runCatching {
                json.jsonArray.mapIndexed { pageIndex, pageObj ->
                    val originObj = pageObj.jsonObject["origin"]!!.jsonObject
                    val actualObj = pageObj.jsonObject["actual"]!!.jsonObject
                    val useActual = pageObj.jsonObject["useActual"]?.jsonPrimitive
                        ?.contentOrNull?.toBooleanStrictOrNull() == true

                    val origin = templates.map { template ->
                        template.restore(
                            documentState = documentState,
                            json = tryParse { originObj[template.id]!! },
                            rowIndex = pageIndex,
                            param = ComputeMetadata(inTable = id)
                        ) as TextState
                    }.toImmutableList()

                    val actual = templates.map { template ->
                        template.restore(
                            documentState = documentState,
                            json = tryParse { actualObj[template.id]!! },
                            rowIndex = pageIndex
                        ) as TextState
                    }.toImmutableList()

                    TablePageState(
                        origin = origin,
                        actual = actual,
                        useActual = useActual
                    )
                }
            }.getOrDefault(
                emptyList()
            )
            return ComparisonTableState(
                id = id,
                name = name,
                maxEntries = maxEntries,
                templateValue = { index -> createEmptyValues(documentState, index).first() },
                documentState = documentState,
                initialValues = values
            )
        }

        private fun createEmptyValues(
            documentState: DocumentState,
            rowIndex: Int
        ): List<TablePageState> {
            return listOf(
                TablePageState(
                    origin = templates.map {
                        it.restore(
                            documentState = documentState,
                            rowIndex = rowIndex
                        ) as TextState
                    }.toImmutableList(),
                    actual = templates.map {
                        it.restore(
                            documentState = documentState,
                            rowIndex = rowIndex
                        ) as TextState
                    }.toImmutableList(),
                    useActual = false
                )
            )
        }
    }

    @Serializable
    @SerialName("repeatable")
    data class Repeatable(
        override val id: String,
        val maxEntries: Int = Int.MAX_VALUE,
        val name: String,
        val templates: List<Template>,
    ) : Template {
        override fun restore(
            documentState: DocumentState,
            json: JsonElement,
            rowIndex: Int,
            param: Any?
        ): FieldState {
            val values = runCatching {
                json.jsonArray.map { group ->
                    templates.map { template ->
                        template.restore(
                            documentState = documentState,
                            json = tryParse { group.jsonObject[template.id]!! },
                            rowIndex = rowIndex,
                            param = (param as? ComputeMetadata)?.copy(inGroup = id)
                        ) as TextState
                    }
                }
            }
                .onFailure {
                    it.printStackTrace()
                }
                .getOrDefault(
                    emptyList()
                )
            return RepeatableState(
                id = id,
                name = name,
                emptyTemplate = { createEmptyTemplate(documentState) },
                initialValue = values,
                maxEntries = maxEntries,
                documentState = documentState
            )
        }

        private fun createEmptyTemplate(documentState: DocumentState): SnapshotStateList<FieldState> {
            return templates.map {
                it.restore(
                    documentState = documentState,
                    rowIndex = 0
                )
            }.toMutableStateList()
        }
    }

    @Serializable
    @SerialName("location")
    data class Location(
        override val id: String,
        val latitude: Text,
        val longitude: Text
    ) : Template {
        override fun restore(
            documentState: DocumentState,
            json: JsonElement,
            rowIndex: Int,
            param: Any?
        ): FieldState {
            return LocationState(
                id = id,
                lat = latitude.restore(
                    documentState = documentState,
                    json = tryParse { json.jsonObject[latitude.id]!!.jsonPrimitive }
                ) as TextState,
                lon = longitude.restore(
                    documentState = documentState,
                    json = tryParse { json.jsonObject[longitude.id]!!.jsonPrimitive }
                ) as TextState,
                documentState = documentState
            )
        }
    }
}

private inline fun tryParse(block: () -> JsonElement): JsonElement {
    return runCatching(block).getOrDefault(JsonNull)
}