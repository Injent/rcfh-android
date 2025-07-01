package ru.rcfh.presentation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import ru.rcfh.MainActivityUiState
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.navigation.AppNavHost

@Composable
fun App(
    uiState: MainActivityUiState,
) {
    AppTheme(
        uiTheme = uiState.uiTheme
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            modifier = Modifier
                .drawWithContent {
                    if (!uiState.loading) {
                        drawContent()
                    }
                }
        ) { innerPadding ->
            AppNavHost(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .focusable()
            )
        }
    }
}