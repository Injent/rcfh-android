package ru.rcfh.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rcfh.MainActivityUiState
import ru.rcfh.blank.presentation.viewer.viewerScreen
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.documents.presentation.pane.documentsScreen
import ru.rcfh.feature.login.presentation.signin.signInScreen
import ru.rcfh.feature.login.presentation.signup.signUpScreen
import ru.rcfh.feature.settings.presentation.settingsScreen

@Composable
fun AppNavHost(
    uiState: MainActivityUiState,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    ObserveAsEvents(Navigator.navigationActions) { event ->
        when (event) {
            is NavigationAction.Navigate -> navController.navigate(event.destination, event.navOptions)
            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    var startScreen by rememberSaveable(saver = StartDestinationSaver) {
        mutableStateOf(Screen.Loading)
    }
    LaunchedEffect(uiState) {
        if (uiState.loading || startScreen != Screen.Loading) return@LaunchedEffect

        startScreen = if (uiState.authed) {
            Screen.Documents()
        } else {
            Screen.SignIn
        }
    }

    NavHost(
        navController = navController,
        startDestination = startScreen,
        modifier = modifier
    ) {
        composable<Screen.Loading> { }
        viewerScreen()
//        formNavigatorScreen()
//        handbookSearchScreen()
//        tableScreen()
//        tableRecordScreen()
//        comparisonTableScreen()
//        summarizeScreen()
        documentsScreen()
        signUpScreen()
        signInScreen()
        settingsScreen()

        composable<Screen.Guide> {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Здесь будет руководство по приложению",
                    style = AppTheme.typography.headline1,
                    color = AppTheme.colorScheme.foreground1
                )
            }
        }
    }
}

private val StartDestinationSaver: Saver<MutableState<Screen>, Int>
    get() = Saver(
        save = {
            when (it.value) {
                is Screen.Loading -> 0
                is Screen.Documents -> 1
                else -> 2
            }
        },
        restore = { screenId ->
            when (screenId) {
                0 -> Screen.Loading
                1 -> Screen.Documents()
                else -> Screen.SignIn
            }.let { mutableStateOf(it) }
        }
    )