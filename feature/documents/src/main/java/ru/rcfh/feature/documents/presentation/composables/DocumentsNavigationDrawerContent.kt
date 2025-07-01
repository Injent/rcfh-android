package ru.rcfh.feature.documents.presentation.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.rcfh.core.account.model.RcfhAccount
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Guide
import ru.rcfh.designsystem.icon.Settings
import ru.rcfh.designsystem.icon.StatusSuccess
import ru.rcfh.designsystem.icon.User
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ScreenRotation
import ru.rcfh.designsystem.util.screenRotation
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.feature.documents.R
import ru.rcfh.feature.documents.presentation.list.DocumentListUiState
import ru.rcfh.navigation.Screen

enum class DrawerDestination(
    val route: Screen,
    @StringRes val label: Int,
    val imageVector: ImageVector,
) {
    GUIDE(route = Screen.Guide, label = R.string.label_guide, imageVector = AppIcons.Guide),
    SETTINGS(route = Screen.Settings, label = R.string.label_settings, imageVector = AppIcons.Settings)
}

@Composable
internal fun DocumentsNavigationDrawerContent(
    uiState: DocumentListUiState,
    onCreateAccountRequest: () -> Unit,
    onChooseAccount: (RcfhAccount) -> Unit,
    onNavigateToDrawer: (DrawerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        windowInsets = DrawerDefaults.windowInsets.exclude(WindowInsets.statusBars),
        drawerContainerColor = AppTheme.colorScheme.background2,
        modifier = modifier
            .widthIn(max = 300.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            val rotation = screenRotation()
            val paddingModifier = Modifier
                .thenIf(rotation == ScreenRotation.LEFT) {
                    displayCutoutPadding()
                }
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppTheme.colorScheme.background1,
                        shape = AppTheme.shapes.defaultTopCarved
                    )
                    .statusBarsPadding()
                    .then(paddingModifier)
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                                + AppTheme.spacing.m,
                        bottom = AppTheme.spacing.m
                    )
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                Icon(
                    imageVector = AppIcons.User,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.foreground2,
                    modifier = Modifier
                        .size(50.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append(uiState.currentAccount?.displayName ?: "")
                        withStyle(
                            AppTheme.typography.body.toSpanStyle()
                        ) {
                            appendLine()
                            append(uiState.currentAccount?.login ?: "")
                        }
                    },
                    style = AppTheme.typography.headline1,
                    color = AppTheme.colorScheme.foreground1
                )
            }

            uiState.accounts.forEach { account ->
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = account.login,
                            style = AppTheme.typography.body,
                        )
                    },
                    icon = {
                        Box {
                            Icon(
                                imageVector = AppIcons.User,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                            if (uiState.currentAccount == account) {
                                Icon(
                                    imageVector = AppIcons.StatusSuccess,
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .offset(x = 4.dp, y = 4.dp)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                        }
                    },
                    shape = AppTheme.shapes.default,
                    selected = false,
                    onClick = {
                        onChooseAccount(account)
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = AppTheme.colorScheme.background2,
                        unselectedTextColor = AppTheme.colorScheme.foreground1,
                        unselectedIconColor = AppTheme.colorScheme.foreground2,
                    ),
                    modifier = Modifier
                        .then(paddingModifier)
                )
            }
            NavigationDrawerItem(
                label = {
                    Text(
                        text = stringResource(R.string.label_addAccount),
                        style = AppTheme.typography.body,
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                    )
                },
                shape = AppTheme.shapes.default,
                selected = false,
                onClick = onCreateAccountRequest,
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = AppTheme.colorScheme.background2,
                    unselectedTextColor = AppTheme.colorScheme.foreground1,
                    unselectedIconColor = AppTheme.colorScheme.foreground2
                ),
                modifier = Modifier
                    .then(paddingModifier)
            )
            HorizontalDivider(
                color = AppTheme.colorScheme.stroke2,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            DrawerDestination.entries.forEach { destination ->
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(destination.label),
                            style = AppTheme.typography.body,
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = destination.imageVector,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    },
                    shape = AppTheme.shapes.default,
                    selected = false,
                    onClick = { onNavigateToDrawer(destination) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = AppTheme.colorScheme.foreground1,
                        unselectedIconColor = AppTheme.colorScheme.foreground2
                    ),
                    modifier = Modifier
                        .then(paddingModifier)
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}