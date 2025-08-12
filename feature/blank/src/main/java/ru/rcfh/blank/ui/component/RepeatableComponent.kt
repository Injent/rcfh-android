package ru.rcfh.blank.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.query
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.state.ArrayElement
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.blank.ui.state.create
import ru.rcfh.blank.ui.style.RepeatableStyle

data class RepeatableGroup(
    val components: ImmutableList<Component>
)

abstract class RepeatableComponent(
    private val rootPath: String,
    private val maxEntries: Int = Int.MAX_VALUE
) : Component(target = "") {
    private var groupCount by mutableIntStateOf(0)
    abstract val name: String

    abstract fun getComponents(basePath: String): List<Component>

    private fun getAllComponents(state: Element): List<RepeatableGroup> {
        val groupsCount = state.queryOrCreate(rootPath, state.create<ArrayElement>()).size

        return List(groupsCount) { index ->
            RepeatableGroup(
                components = getComponents(basePath = "$rootPath[$index]").toImmutableList()
            )
        }
    }

    override fun calculate(documentScope: DocumentScope, state: Element) {
        val groupCount = state.query(rootPath)?.array?.size ?: 0
        if (groupCount != this.groupCount) {
            this.groupCount = groupCount
            documentScope.putRepeatableComponents(
                repeatablePath = rootPath,
                groups = getAllComponents(state)
            )
        }
    }

    @Composable
    override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
        RepeatableStyle(
            documentScope = documentScope,
            state = state,
            rootPath = rootPath,
            maxEntries = maxEntries,
            enabled = enabled,
            navigator = navigator,
            name = name,
        )
    }
}