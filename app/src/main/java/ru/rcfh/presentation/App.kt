package ru.rcfh.presentation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.navigation.MainNavGraph

@Composable
fun App(

) {
    AppTheme {
        Scaffold(
            contentWindowInsets = WindowInsets(0)
        ) { innerPadding ->
            MainNavGraph(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .focusable()
            )
        }
    }
}