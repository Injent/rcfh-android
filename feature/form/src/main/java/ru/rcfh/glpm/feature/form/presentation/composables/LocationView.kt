package ru.rcfh.glpm.feature.form.presentation.composables

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rcfh.core.sdui.state.LocationState
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Location
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.unwrap
import ru.rcfh.glpm.feature.form.R

@Composable
fun LocationView(
    state: LocationState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
        modifier = modifier
    ) {
        if (state.isReceiving) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 30.dp)
            ) {
                CircularProgressIndicator(
                    color = AppTheme.colorScheme.foreground2,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
            Spacer(Modifier.weight(1f))
        } else {
            AppTextField(
                value = state.lat.value,
                onValueChange = { state.lat.value = it },
                placeholder = stringResource(R.string.placeholder_float),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                error = state.lat.error,
                label = state.lat.label,
                modifier = Modifier
                    .weight(1f)
            )
            AppTextField(
                value = state.lon.value,
                onValueChange = { state.lon.value = it },
                placeholder = stringResource(R.string.placeholder_float),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                error = state.lon.error,
                label = state.lon.label,
                modifier = Modifier
                    .weight(1f)
            )
        }

        Column {
            Text(
                text = "",
                style = AppTheme.typography.callout.copy(lineHeight = 12.sp),
                color = AppTheme.colorScheme.foreground1,
                modifier = Modifier
                    .padding(bottom = AppTheme.spacing.sNudge)
            )
            AppIconButton(
                icon = AppIcon(
                    icon = AppIcons.Location,
                    onClick = {
                        state.request(context.unwrap() as ComponentActivity)
                    },
                )
            )
        }
    }
}