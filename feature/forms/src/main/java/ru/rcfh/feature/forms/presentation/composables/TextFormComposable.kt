package ru.rcfh.feature.forms.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.TextState

@Composable
fun TextFormComposable(
    state: TextState,
    template: Template.Text,
    onStateChange: (TextState) -> Unit,
    modifier: Modifier = Modifier,
    onCard: Boolean = false,
) {
    TextComposable(
        value = state.value,
        onValueChange = { onStateChange(TextState(value = it, id = template.id)) },
        template = template,
        modifier = modifier,
        onCard = onCard
    )
}