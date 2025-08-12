package ru.rcfh.blank.ui.component

import androidx.compose.runtime.Composable
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.queryapi.update
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.designsystem.component.DateTextField

abstract class DateComponent(
    target: String,
    protected val format: String = "dd.MM.yyyy",
    private val onCard: Boolean = false,
) : Component(target) {
    abstract val label: String
    private val placeholder = format
        .replace('y', 'Г', ignoreCase = true)
        .replace('d', 'Д', ignoreCase = true)

    @Composable
    override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
        DateTextField(
            value = state.queryOrCreate(target, ""),
            onValueChange = { newValue ->
                state.update(target, newValue)
            },
            format = format,
            label = label,
            placeholder = placeholder,
            enabled = enabled,
            onCard = onCard
        )
    }
}