package ru.rcfh.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.model.UiTheme
import ru.rcfh.datastore.model.Prefs
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.settings.R
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.settingsScreen() {
    composable<Screen.Settings> {
        SettingsRoute()
    }
}

@Composable
private fun SettingsRoute() {
    val viewModel = koinViewModel<SettingsViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onBack = viewModel::back,
        onUpdatePrefs = viewModel::onUpdatePrefs
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onUpdatePrefs: (Prefs) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_settings),
                        style = AppTheme.typography.title3,
                        color = AppTheme.colorScheme.foreground1
                    )
                },
                navigationIcon = {
                    AppBackButton(
                        onClick = onBack
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .selectableGroup()
        ) {
            UiTheme.entries.forEach { uiTheme ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(56.dp)
                        .selectable(
                            selected = uiState.prefs.uiTheme == uiTheme,
                            onClick = {
                                onUpdatePrefs(uiState.prefs.copy(uiTheme = uiTheme))
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    RadioButton(
                        selected = uiState.prefs.uiTheme == uiTheme,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AppTheme.colorScheme.foreground,
                            unselectedColor = AppTheme.colorScheme.foreground3,
                        )
                    )
                    Text(
                        text = when (uiTheme) {
                            UiTheme.LIGHT -> R.string.option_light
                            UiTheme.FOREST -> R.string.option_forest
                        }.let { stringResource(it) },
                        style = AppTheme.typography.calloutButton,
                        color = AppTheme.colorScheme.foreground1
                    )
                }
            }
        }
    }
}