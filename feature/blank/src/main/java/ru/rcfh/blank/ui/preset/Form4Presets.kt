package ru.rcfh.blank.ui.preset

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.rcfh.blank.presentation.comparisontable.ComparisonTableScreen
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.component.BasicInputComponent
import ru.rcfh.blank.ui.component.Component
import ru.rcfh.blank.ui.queryapi.query
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.state.ArrayElement
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.blank.ui.state.create
import ru.rcfh.designsystem.component.AppItemCard

data class Row(
    val components: ImmutableList<Component>
)

fun initForm4Components(documentScope: DocumentScope) {
    listOf(
        object : Component(target = "") {
            private var rowCount by mutableIntStateOf(0)
            private val tableRootPath = "$.forms.4.table"
            private val maxEntries = Int.MAX_VALUE

            private fun components(basePath: String): List<Component> = listOf(
                object : BasicInputComponent() {
                    
                }
            )

            private fun getAllComponents(state: Element): List<Row> {
                val rows = state.queryOrCreate("$tableRootPath.rows", state.create<ArrayElement>())

                return List(rows.size) { index ->
                    Row(
                        components = components("$tableRootPath.rows[$index]").toImmutableList()
                    )
                }
            }

            override fun calculate(documentScope: DocumentScope, state: Element) {
                val rowCount = state.query("$.forms.2.species_specs.rows")?.array?.size ?: 0

                if (this.rowCount != rowCount) {
                    this.rowCount = rowCount

                    documentScope.putTableComponents(
                        tablePath = tableRootPath,
                        rows = getAllComponents(state)
                    )
                }
            }

            @Composable
            override fun Content(
                state: Element,
                navigator: ViewerNavigator,
                documentScope: DocumentScope
            ) {
                AppItemCard(
                    label = "Таблица",
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
    )
        .apply { documentScope.registerComponents(4, this) }
        .forEach { it.init(documentScope) }
}