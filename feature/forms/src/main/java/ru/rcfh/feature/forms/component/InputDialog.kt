package ru.rcfh.feature.forms.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ClearFocusWithImeEffect

class InputDialogState(
    val title: String,
    val hint: String?,
    val textFieldState: TextFieldState,
    val inputTransformation: InputTransformation? = null,
    val placeholder: String = "",
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val trailingContent: @Composable () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDialog(
    state: InputDialogState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var focusedOnInit by remember { mutableStateOf(false) }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        // Inside dialog effect
        ClearFocusWithImeEffect()
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    AppTheme.colorScheme.background1,
                    AppTheme.shapes.default
                )
                .padding(AppTheme.spacing.m)
        ) {
            Text(
                text = state.title,
                style = AppTheme.typography.subheadline,
                color = AppTheme.colorScheme.foreground2
            )
            val focusRequester = remember { FocusRequester() }
            AppTextField(
                state = state.textFieldState,
                inputTransformation = state.inputTransformation,
                onKeyboardAction = {
                    onDismissRequest()
                },
                placeholder = state.placeholder,
                keyboardOptions = state.keyboardOptions.copy(imeAction = ImeAction.Done),
                trailingIcon = state.trailingContent,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusEvent { state ->
                        if (!state.isFocused && focusedOnInit) {
                            onDismissRequest()
                        }
                    }
            )
            LaunchedEffect(Unit) {
                delay(100)
                focusRequester.requestFocus()
                focusedOnInit = true
            }
            state.hint?.let { text ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AppTheme.colorScheme.foreground2,
                        modifier = Modifier
                            .size(16.dp)
                            .offset(y = 2.dp)
                    )
                    Text(
                        text = text,
                        style = AppTheme.typography.callout,
                        color = AppTheme.colorScheme.foreground2
                    )
                }
            }
        }
    }
}