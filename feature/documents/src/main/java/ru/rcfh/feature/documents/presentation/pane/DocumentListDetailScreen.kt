package ru.rcfh.feature.documents.presentation.pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.screenRotation
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.feature.documents.R
import ru.rcfh.feature.documents.presentation.composables.DocumentsNavigationDrawerContent
import ru.rcfh.feature.documents.presentation.details.DocumentRoute
import ru.rcfh.feature.documents.presentation.list.DocumentListRoute
import ru.rcfh.feature.documents.presentation.list.DocumentListViewModel
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen
import ru.rcfh.navigation.toRoute

@Serializable
internal object DocumentPlaceholderRoute

@Serializable
internal data class DocumentScreen(val documentId: Int, val isNew: Boolean = false)

fun NavGraphBuilder.documentsScreen() {
    composable<Screen.Documents>(
        enterTransition = {
            when (initialState.destination.toRoute()) {
                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.toRoute()) {
                else -> null
            }
        }
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<Screen.Documents>()

        DocumentListDetailScreen(
            selectedDocumentId = route.documentId,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DocumentListDetailScreen(
    selectedDocumentId: Int?,
) {
    val scope = rememberCoroutineScope()
    val screenRotation = screenRotation()
    val adaptive = currentWindowAdaptiveInfo()
    val systemDirective = calculatePaneScaffoldDirective(adaptive)
    val customDirective = PaneScaffoldDirective(
        maxHorizontalPartitions = systemDirective.maxHorizontalPartitions,
        horizontalPartitionSpacerSize = 0.dp,
        maxVerticalPartitions = systemDirective.maxVerticalPartitions,
        verticalPartitionSpacerSize = systemDirective.verticalPartitionSpacerSize,
        excludedBounds = systemDirective.excludedBounds,
        defaultPanePreferredWidth = 400.dp
    )
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = customDirective,
        initialDestinationHistory = listOfNotNull(
            ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List),
            ThreePaneScaffoldDestinationItem<Nothing>(ListDetailPaneScaffoldRole.Detail).takeIf {
                selectedDocumentId != null
            }
        )
    )

    BackHandler(listDetailNavigator.canNavigateBack()) {
        scope.launch {
            listDetailNavigator.navigateBack()
        }
    }
    var documentRoute by rememberSaveable(saver = DocumentScreen.Saver) {
        val route = selectedDocumentId?.let { DocumentScreen(documentId = it) }
            ?: DocumentPlaceholderRoute
        mutableStateOf(route)
    }

    fun onDocumentClick(documentId: Int, isNew: Boolean) {
        documentRoute = DocumentScreen(documentId = documentId, isNew = isNew)
        scope.launch {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        }
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val viewModel = koinViewModel<DocumentListViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var drawerWidth by remember { mutableIntStateOf(0) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DocumentsNavigationDrawerContent(
                onCreateAccountRequest = {
                    documentRoute = DocumentPlaceholderRoute
                    scope.launch {
                        Navigator.navigate(Screen.SignIn) { launchSingleTop = true }
                    }
                },
                onNavigateToDrawer = { destination ->
                    documentRoute = DocumentPlaceholderRoute
                    scope.launch {
                        drawerState.close()
                        Navigator.navigate(destination.route)
                    }
                },
                onChooseAccount = { account ->
                    documentRoute = DocumentPlaceholderRoute
                    scope.launch {
                        drawerState.close()
                        viewModel.onChooseAccount(account)
                    }
                },
                uiState = uiState,
                modifier = Modifier
                    .onSizeChanged { size ->
                        drawerWidth = size.width
                    }

            )
        },
    ) {
        ListDetailPaneScaffold(
            directive = listDetailNavigator.scaffoldDirective,
            scaffoldState = listDetailNavigator.scaffoldState,
            listPane = {
                AnimatedPane {
                    DocumentListRoute(
                        viewModel = viewModel,
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        },
                        onCreateDocument = { id ->
                            onDocumentClick(documentId = id, isNew = true)
                        },
                        onSelectDocument = { id ->
                            onDocumentClick(documentId = id, isNew = false)
                        }
                    )
                }
            },
            paneExpansionDragHandle = {
                VerticalDivider(
                    color = AppTheme.colorScheme.stroke2,
                    thickness = 1.2.dp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = AppTheme.spacing.xxl)
                )
            },
            detailPane = {
                AnimatedPane {
                    AnimatedContent(documentRoute) { route ->
                        when (route) {
                            is DocumentPlaceholderRoute -> DocumentPlaceholder()
                            is DocumentScreen -> {
                                DocumentRoute(
                                    documentId = route.documentId,
                                    isNew = route.isNew,
                                    showBackButton = !listDetailNavigator.isListPaneVisible(),
                                    onBack = {
                                        scope.launch {
                                            documentRoute = DocumentPlaceholderRoute
                                            if (!listDetailNavigator.navigateBack()) {
                                                Navigator.navigateUp()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .displayCutoutPadding()
                .thenIf(screenRotation != ScreenRotation.NONE) {
                    navigationBarsPadding()
                }
                .offset {
                    IntOffset(
                        x = try {
                            drawerWidth + drawerState.currentOffset.toInt()
                        } catch (_: IllegalArgumentException) {
                            0
                        },
                        y = 0
                    )
                },
        )
    }
}

@Composable
fun DocumentPlaceholder() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(AppTheme.spacing.l)
    ) {
        Text(
            text = stringResource(R.string.placeholder_noDocumentsOpened),
            style = AppTheme.typography.body,
            color = AppTheme.colorScheme.foreground2,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 200.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded

private val DocumentScreen.Companion.Saver: Saver<MutableState<Any>, Int>
    get() = Saver(
        save = {
            (it.value as? DocumentScreen)?.documentId ?: -1
        },
        restore = {
            mutableStateOf(
                if (it == -1) {
                    DocumentPlaceholderRoute
                } else {
                    DocumentScreen(documentId = it)
                }
            )
        }
    )