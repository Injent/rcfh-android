package ru.rcfh.blank.ui.component

import androidx.compose.runtime.Composable
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.queryOrDefault
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.blank.ui.style.LinkedStyle

abstract class LinkedComponent(target: String) : Component(target = target) {
    abstract val label: String

    @Composable
    override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
        LinkedStyle(
            label = label,
            value = state.queryOrDefault(target, "-"),
            hasLine = true
        )
    }
}