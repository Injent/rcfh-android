package ru.rcfh.feature.home.presentation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.account.model.RcfhAccount
import ru.rcfh.designsystem.component.AppItemCard
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Document
import ru.rcfh.designsystem.icon.Menu
import ru.rcfh.designsystem.icon.User
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.home.presentation.component.DrawerDestination
import ru.rcfh.feature.home.presentation.component.HomeNavigationDrawerContent
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.homeScreen() {
    composable<Screen.Home>(
        enterTransition = {
            when (initialState.destination.route) {
                Screen.Loading::class.qualifiedName -> fadeIn(animationSpec = tween(1000))
                else -> null
            }
        }
    ) {
        HomeRoute()
    }
}

@Composable
private fun HomeRoute() {
    val viewModel = koinViewModel<HomeViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        onNavigateTo = { destination ->
            when (destination) {
                DrawerDestination.DOCUMENTS -> Screen.Documents()
                DrawerDestination.SETTINGS -> Screen.Settings
            }.let(viewModel::navigate)
        },
        onCreateAccountRequest = {
            viewModel.navigate(Screen.SignIn)
        },
        onChooseAccount = viewModel::onChooseAccount,
        uiState = uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onNavigateTo: (DrawerDestination) -> Unit,
    onChooseAccount: (RcfhAccount) -> Unit,
    onCreateAccountRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeNavigationDrawerContent(
                onCreateAccountRequest = onCreateAccountRequest,
                onNavigateToDrawer = {
                    scope.launch {
                        drawerState.close()
                        onNavigateTo(it)
                    }
                },
                onChooseAccount = {
                    scope.launch {
                        drawerState.close()
                        onChooseAccount(it)
                    }
                },
                uiState = uiState
            )
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = AppIcons.Menu,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.foreground1,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        }
                    },
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = AppTheme.spacing.l)
                                .clickable {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                        ) {
                            Text(
                                text = uiState.currentAccount?.displayName ?: "",
                                style = AppTheme.typography.headline2,
                                color = AppTheme.colorScheme.foreground1
                            )
                            Icon(
                                imageVector = AppIcons.User,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.foreground2,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            },
            modifier = Modifier
                .displayCutoutPadding()
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                AppItemCard(
                    label = "Документы",
                    icon = AppIcons.Document,
                    onClick = { onNavigateTo(DrawerDestination.DOCUMENTS) },
                )
            }
        }
    }
}

