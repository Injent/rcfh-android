package ru.rcfh.designsystem.component

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radusalagean.infobarcompose.BaseInfoBarMessage
import com.radusalagean.infobarcompose.InfoBar
import com.radusalagean.infobarcompose.InfoBarEasing
import com.radusalagean.infobarcompose.InfoBarSlideEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Cross
import ru.rcfh.designsystem.theme.AppTheme

class SnackbarMessage(
    val text: String,
    val icon: ImageVector,
    val tint: Color,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null,
    override val displayTimeSeconds: Int? = 4,
) : BaseInfoBarMessage() {
    override val containsControls = false
}

object SnackbarController {
    private val _message = MutableStateFlow<SnackbarMessage?>(null)
    val message = _message.asStateFlow()

    fun show(message: SnackbarMessage) {
        _message.value = message
    }

    fun dismiss() {
        _message.value = null
    }
}

private val snackbarContent: @Composable (SnackbarMessage, () -> Unit) -> Unit = { message, onDismiss ->
    DefaultSnackbar(
        text = message.text,
        icon = AppIcon(icon = message.icon, tint = message.tint),
        onDismissRequest = {
            onDismiss()
            message.onDismiss?.invoke()
        },
        actionLabel = message.actionLabel,
        onAction = { message.onAction?.invoke() },
        dismissible = message.onDismiss != null
    )
}

@Composable
fun AppSnackbarHost(
    modifier: Modifier = Modifier
) {
    val snackbarMessage by SnackbarController.message.collectAsStateWithLifecycle()

    InfoBar(
        wrapInsideExpandedBox = false,
        backgroundColor = Color.Transparent,
        enterTransitionMillis = 400,
        elevation = 0.dp,
        fadeEffect = false,
        slideEffect = InfoBarSlideEffect.FROM_BOTTOM,
        slideEffectEasing = InfoBarEasing(EaseOutBack),
        scaleEffect = false,
        offeredMessage = snackbarMessage,
        content = { message ->
            snackbarContent(message) { SnackbarController.dismiss() }
        },
        modifier = modifier
            .zIndex(1f)
            .padding(
                start = AppTheme.spacing.l,
                end = AppTheme.spacing.l,
                bottom = AppTheme.spacing.l,
            ),
    ) { SnackbarController.dismiss() }
}

@Composable
private fun DefaultSnackbar(
    text: String,
    icon: AppIcon,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit,
    dismissible: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 400.dp)
            .background(AppTheme.colorScheme.backgroundToast, AppTheme.shapes.default)
            .padding(AppTheme.spacing.l)
    ) {
        Icon(
            imageVector = icon.icon,
            contentDescription = null,
            tint = icon.tint ?: Color.Unspecified,
            modifier = Modifier
                .align(Alignment.Top)
                .size(20.dp)
        )

        Text(
            text = text,
            style = AppTheme.typography.footnote,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(top = 2.dp)
        )

        actionLabel?.let {
            Text(
                text = it,
                style = AppTheme.typography.subheadlineButton,
                color = AppTheme.colorScheme.foregroundWarning,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .pointerInput(onAction) {
                        detectTapGestures(
                            onTap = {
                                onAction()
                            }
                        )
                    }
            )
        }

        if (dismissible) {
            Icon(
                imageVector = AppIcons.Cross,
                contentDescription = null,
                tint = AppTheme.colorScheme.foreground3,
                modifier = Modifier
                    .align(Alignment.Top)
                    .size(20.dp)
                    .pointerInput(onDismissRequest) {
                        detectTapGestures {
                            onDismissRequest()
                        }
                    }
            )
        }
    }
}