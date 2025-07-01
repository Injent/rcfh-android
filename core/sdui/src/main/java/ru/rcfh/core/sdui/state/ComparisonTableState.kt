package ru.rcfh.core.sdui.state

import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonArray
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.ErrorAddress
import ru.rcfh.core.sdui.common.IndexAwareStateList
import ru.rcfh.core.sdui.event.AddPage
import ru.rcfh.core.sdui.event.RemovePage

class ComparisonTableState(
    override val id: String,
    val name: String,
    val maxEntries: Int,
    private val templateValue: (Int) -> TablePageState,
    documentState: DocumentState,
    initialValues: List<TablePageState>
) : FieldState(documentState), Container {
    val pages = IndexAwareStateList(initialValues.toMutableStateList())

    fun addPage() {
        pages.add(templateValue(pages.size))
        with(pages.last()) {
            // Updating the value so that third-party tables notice the update
            useActual = useActual
        }
        document.postEvent(AddPage(templateId = id))
    }

    fun removePage(page: Int) {
        pages.removeAt(page)
        document.postEvent(RemovePage(templateId = id, rowIndex = page))
        refill()
    }

    init {
        refill()
    }

    private fun refill() {
        pages.forEach { page -> page.useActual = page.useActual }
    }

    override fun isValid() = pages.all { page ->
        if (page.useActual) {
            page.actual.all { it.isValid() }
        } else {
            page.origin.all { it.isValid() }
        }
    }

    operator fun get(pageIndex: Int): TablePageState? {
        return pages.getOrNull(pageIndex)
    }

    override fun items(): List<FieldState> {
        return pages.flatMap { page ->
            if (page.useActual) page.actual else page.origin
        }
    }

    override fun detectErrors(): List<DetectedError> {
        return pages.flatMapIndexed { rowIndex, page ->
            if (page.useActual) {
                page.actual.mapNotNull {
                    it.detectErrors().singleOrNull()?.copy(
                        address = ErrorAddress(
                            parentId = id,
                            rowIndex = rowIndex
                        )
                    )
                }
            } else {
                page.origin.mapNotNull {
                    it.detectErrors().singleOrNull()?.copy(
                        address = ErrorAddress(
                            parentId = id,
                            rowIndex = rowIndex
                        )
                    )
                }
            }
        }
    }

    override fun save(): JsonElement {
        return buildJsonArray {
            pages.forEach { page ->
                add(page.save())
            }
        }
    }
}