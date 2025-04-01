package ru.rcfh.feature.forms.presentation.roweditor

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.compose.ProvideStateStorage
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.component.RepeatableInput
import ru.rcfh.feature.forms.component.TextInput
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.rowEditorScreen() {
    composable<Screen.RowEditor> { RowEditorRoute() }
}

@Composable
private fun RowEditorRoute() {
    val viewModel = koinViewModel<RowEditorViewModel>()
    LaunchedEffect(Unit) {
        viewModel.load()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProvideStateStorage(
        cacheStorage = viewModel.cacheStorage,
        draftId = uiState.draftId
    ) {
        RowEditorScreen(
            uiState = uiState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowEditorScreen(
    uiState: RowEditorUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    AppBackButton(
                        onClick = Navigator::navigateUp
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(0),
        modifier = Modifier
            .imePadding()
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
            contentPadding = PaddingValues(
                start = WindowInsets.displayCutout.asPaddingValues().calculateStartPadding(
                    LayoutDirection.Rtl),
                end = WindowInsets.displayCutout.asPaddingValues().calculateEndPadding(
                    LayoutDirection.Rtl),
                top = AppTheme.spacing.l,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    + AppTheme.spacing.l
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .focusGroup()
        ) {
            item {
                Text(
                    text = uiState.template?.name ?: "",
                    style = AppTheme.typography.title3,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                        .padding(bottom = AppTheme.spacing.xl)
                )
            }
            itemsIndexed(
                items = uiState.template?.columns ?: emptyList(),
                key = { index, _ -> index }
            ) { _, template ->
                when (template) {
                    is Template.Repeatable -> {
                        RepeatableInput(
                            template = template,
                            row = uiState.row
                        )
                    }
                    is Template.Text -> {
                        TextInput(
                            template = template,
                            parentTemplateId = uiState.rootTemplateId,
                            row = uiState.row,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppTheme.spacing.l)
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}