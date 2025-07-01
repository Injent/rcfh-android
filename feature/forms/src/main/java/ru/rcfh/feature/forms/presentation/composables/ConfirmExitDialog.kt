package ru.rcfh.feature.forms.presentation.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.rcfh.designsystem.theme.AppTheme

@Composable
fun ConfirmExitDialog(
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = AppTheme.colorScheme.background1,
        modifier = modifier,
        title = {
            Text(
                text = "Закончить заполнение?",
                style = AppTheme.typography.headline1
            )
        },
        text = {
            Text(
                text = "Остались еще не заполненные или неправильно заполненные формы",
                style = AppTheme.typography.callout
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colorScheme.backgroundBrand,
                    contentColor = AppTheme.colorScheme.foregroundOnBrand
                )
            ) {
                Text(
                    text = "Закончить"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppTheme.colorScheme.foreground
                )
            ) {
                Text(
                    text = "Отмена"
                )
            }
        }
    )
}