package ru.rcfh.blank.presentation.viewer

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.rcfh.blank.presentation.blank.BlankComposable
import ru.rcfh.blank.presentation.comparisontable.ComparisonTableRoute
import ru.rcfh.blank.presentation.comparisontable.ComparisonTableScreen
import ru.rcfh.blank.presentation.navbar.NavbarComposable
import ru.rcfh.blank.presentation.referencesearch.ReferenceSearchRoute
import ru.rcfh.blank.presentation.referencesearch.ReferenceSearchScreen
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.navigation.Screen

@Serializable
data object FormNavigator : NavKey

fun NavGraphBuilder.viewerScreen() {
    composable<Screen.Viewer> {
        val route = it.toRoute<Screen.Viewer>()

        ViewerRoute(
            initialFormId = route.formId,
            documentId = route.documentId
        )
    }
}

@Composable
fun ViewerRoute(initialFormId: Int, documentId: Int) {
    val viewModel = koinViewModel<ViewerViewModel> {
        parametersOf(
            ViewerViewModelParams(
                documentId = documentId,
                initialFormId = initialFormId
            )
        )
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ViewerScreen(
        uiState = uiState,
        onSelect = viewModel::selectTab,
        onReplace = viewModel::replaceTab,
    )
}

@Composable
private fun ViewerScreen(
    uiState: ViewerUiState,
    onSelect: (id: Int) -> Unit,
    onReplace: (oldId: Int, newId: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val navigator = rememberViewerNavigator(startDestination = FormNavigator)

    NavDisplay(
        backStack = navigator.backStack,
        onBack = {
            navigator.goBack()
        },
        entryDecorators = listOf(
            rememberViewModelStoreNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberSceneSetupNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<ReferenceSearchScreen> { key ->
                if (uiState is ViewerUiState.Success) {
                    ReferenceSearchRoute(
                        state = uiState.document.state,
                        route = key,
                        navigator = navigator
                    )
                }
            }
            entry<ComparisonTableScreen> { key ->
                if (uiState is ViewerUiState.Success) {
                    ComparisonTableRoute(
                        documentScope = uiState.document.documentScope,
                        tableRootPath = key.tableRootPath,
                        maxEntries = key.maxEntries,
                        pages = uiState.document.comparisonTableComponents[key.tableRootPath] ?: persistentListOf(),
                        state = uiState.document.state,
                        navigator = navigator
                    )
                }
            }
            entry<FormNavigator> {
                Scaffold(
                    topBar = {
                        if (uiState is ViewerUiState.Success) {
                            NavbarComposable(
                                selectedTab = uiState.selectedTab,
                                formTabs = uiState.formTabs,
                                onSelect = onSelect,
                                onReplace = onReplace,
                                modifier = Modifier
                                    .padding(top = AppTheme.spacing.s)
                                    .padding(horizontal = AppTheme.spacing.l)
                            )
                        }
                    },
                    modifier = Modifier
                        .statusBarsPadding()
                        .imePadding()
                ) { innerPadding ->
                    BlankComposable(
                        viewerUiState = uiState,
                        innerPadding = innerPadding,
                        navigator = navigator
                    )
                }
            }
        }
    )
}

@Composable
private fun rememberViewerNavigator(startDestination: NavKey): ViewerNavigator {
    val backStack = rememberNavBackStack(startDestination)
    val coroutineScope = rememberCoroutineScope()
    return remember {
        ViewerNavigator(
            backStack = backStack,
            scope = coroutineScope
        )
    }
}