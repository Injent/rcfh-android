package ru.rcfh.blank.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import ru.rcfh.blank.presentation.comparisontable.ComparisonTableScreen
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.query
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.queryapi.queryOrDefault
import ru.rcfh.blank.ui.state.ArrayElement
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.blank.ui.state.create
import ru.rcfh.designsystem.component.AppItemCard

data class TablePage(
    val actual: List<Component>,
    val origin: List<Component>
)

abstract class ComparisonTableComponent(
    protected val tableRootPath: String,
    private val maxEntries: Int = Int.MAX_VALUE
) : Component(target = "") {
    private var pageCount by mutableIntStateOf(0)
    abstract val name: String

    abstract fun getComponents(basePath: String, useActualPath: String): List<Component>

    private fun getAllComponents(state: Element): List<TablePage> {
        val rows = state.queryOrCreate("$tableRootPath.rows", state.create<ArrayElement>())

        return List(rows.size) { index ->
            TablePage(
                actual = getComponents(
                    basePath = "$tableRootPath.rows[$index].actual",
                    useActualPath = "$tableRootPath.rows[$index].useActual"
                ),
                origin = getComponents(
                    basePath = "$tableRootPath.rows[$index].origin",
                    useActualPath = "$tableRootPath.rows[$index].useActual"
                )
            )
        }
    }

    fun isChildEnabled(state: Element, rowPath: String, useActualPath: String): Boolean {
        val useActual = state.queryOrDefault(useActualPath, "false")
            .toBooleanStrictOrNull() ?: false

        return if (useActual) {
            rowPath.endsWith("actual")
        } else {
            rowPath.endsWith("origin")
        }
    }

    override fun calculate(documentScope: DocumentScope, state: Element) {
        val rowCount = state.query("$tableRootPath.rows")?.array?.size ?: 0
        if (rowCount != this.pageCount) {
            this.pageCount = rowCount
            documentScope.putComparisonTableComponents(
                tableRootPath,
                getAllComponents(state)
                    .onEach { page ->
                        page.actual.forEach { it.init(documentScope) }
                        page.origin.forEach { it.init(documentScope) }
                    }
            )
        }
    }

    @Composable
    override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
        AppItemCard(
            label = name,
            onClick = {
                navigator.goTo(
                    ComparisonTableScreen(
                        tableRootPath = tableRootPath,
                        maxEntries = maxEntries
                    )
                )
            }
        )
    }
}