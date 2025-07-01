package ru.rcfh.feature.forms.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.LocationState
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Location
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.R
import ru.rcfh.feature.forms.state.rememberLocationReceiver

@Composable
fun LocationForm(
    state: LocationState,
    onStateChange: (LocationState) -> Unit,
    template: Template.Location,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val locationReceiver = rememberLocationReceiver()

    fun onValueChange(index: Int, value: String) {
        if (index == 0) {
            onStateChange(state.copy(latitude = value))
        } else {
            onStateChange(state.copy(longitude = value))
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.m),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier,
    ) {
        repeat(2) { index ->
            var errorMsg by remember { mutableStateOf<String?>(null) }

            val value = if (index == 0) {
                state.latitude
            } else {
                state.longitude
            } ?: ""

            val textFieldState = remember { TextFieldState(initialText = value) }

            LaunchedEffect(value) {
                if (value != textFieldState.text.toString()) {
                    textFieldState.edit { replace(0, length, value) }
                }
            }

            LaunchedEffect(textFieldState.text) {
                val newText = textFieldState.text.toString().trim()
                if (newText != value) {
                    if (template.required) {
                        if (newText.isBlank()) {
                            errorMsg = context.getString(R.string.error_emptyValue)
                            onValueChange(index, newText)
                            return@LaunchedEffect
                        } else {
                            errorMsg = null
                        }
                    }

                    onValueChange(index, newText)
                }
            }

            AppTextField(
                state = textFieldState,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = "0.0",
                error = errorMsg,
                label = stringResource(
                    if (index == 0) {
                        R.string.label_latitude
                    } else R.string.lable_longitude
                ),
                inputTransformation = {
                    if (!asCharSequence().all { it.isDigit() || it == '.' }) {
                        revertAllChanges()
                    }
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
        AppIconButton(
            icon = AppIcon(
                icon = AppIcons.Location,
                onClick = {
                    scope.launch {
                        locationReceiver.receiveLocation()?.let {
                            onStateChange(
                                LocationState(
                                    id = template.id,
                                    latitude = it.latitude.toString(),
                                    longitude = it.longitude.toString()
                                )
                            )
                        }
                    }
                }
            )
        )
    }
}