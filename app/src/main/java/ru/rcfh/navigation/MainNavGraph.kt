package ru.rcfh.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ru.rcfh.feature.forms.presentation.form.formsScreen
import ru.rcfh.feature.forms.presentation.handbooksearch.handbookDialog
import ru.rcfh.feature.forms.presentation.roweditor.rowEditorScreen
import ru.rcfh.feature.forms.presentation.sectioneditor.sectionEditor
import ru.rcfh.feature.templates.presentation.templatesScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    ObserveAsEvents(Navigator.navigationActions) { event ->
        when (event) {
            is NavigationAction.Navigate -> navController.navigate(event.destination, event.navOptions)
            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Templates,
        modifier = modifier
    ) {
        templatesScreen()
        formsScreen()
        rowEditorScreen()
        handbookDialog()
        sectionEditor()
    }
}