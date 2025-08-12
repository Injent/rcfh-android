package ru.rcfh.blank.ui.style

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Location
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.unwrap
import ru.rcfh.glpm.feature.blank.R
import kotlin.math.pow
import kotlin.math.roundToLong

interface OnLocationChange {
    fun onLatitudeChange(lat: String)
    fun onLongitudeChange(lon: String)
    fun onMslChange(msl: String)
}

@Composable
fun LocationStyle(
    lat: String,
    lon: String,
    latError: String?,
    lonError: String?,
    onChange: OnLocationChange,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val locationState = rememberLocationState()

    LaunchedEffect(locationState.latitude) {
        locationState.latitude
            ?.roundTo(6)
            ?.let { onChange.onLatitudeChange(it.toString()) }
    }
    LaunchedEffect(locationState.longitude) {
        locationState.longitude
            ?.roundTo(6)
            ?.let { onChange.onLongitudeChange(it.toString()) }
    }
    LaunchedEffect(locationState.msl) {
        locationState.msl
            ?.let { onChange.onMslChange(it.toString()) }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
        modifier = modifier
    ) {
        AppTextField(
            value = lat,
            onValueChange = { newLat ->
                onChange.onLatitudeChange(newLat)
            },
            placeholder = stringResource(R.string.feature_blank_placeholder_number),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            enabled = !locationState.isReceiving,
            error = latError,
            label = "Широта",
            modifier = Modifier
                .weight(1f)
        )
        AppTextField(
            value = lon,
            onValueChange = { newLon ->
                onChange.onLatitudeChange(newLon)
            },
            placeholder = stringResource(R.string.feature_blank_placeholder_number),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            error = lonError,
            enabled = !locationState.isReceiving,
            label = "Долгота",
            modifier = Modifier
                .weight(1f)
        )

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
                        locationState.request(context.unwrap() as ComponentActivity)
                    },
                )
            )
        }
    }
}

private fun Double.roundTo(decimals: Int): Double {
    require(decimals >= 0) { "Decimals must be non-negative" }
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToLong() / factor
}