package ru.rcfh.blank.presentation.blank

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.rcfh.designsystem.theme.AppTheme

@Composable
fun BlankComposableError() {
    Box(Modifier.fillMaxSize()) {
        Text(
            text = "Error",
            style = AppTheme.typography.headline1,
            color = AppTheme.colorScheme.foregroundError
        )
    }
}