package ru.rcfh.feature.templates.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.designsystem.component.AppItemCard
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.templatesScreen() {
    composable<Screen.Templates> { TemplatesRoute() }
}

@Composable
private fun TemplatesRoute() {
    val viewModel = koinViewModel<TemplatesViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TemplatesScreen(
        onNavigateToForms = { templateId ->
            Navigator.navigate(Screen.Forms(formId = templateId))
        },
        uiState = uiState,
    )
}

@Composable
private fun TemplatesScreen(
    onNavigateToForms: (Int) -> Unit,
    uiState: TemplatesUiState
) {
    Scaffold(

    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(AppTheme.spacing.l),
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 700.dp)
                .padding(innerPadding)
        ) {
            items(
                items = uiState.templates
            ) { template ->
                AppItemCard(
                    onClick = {
                        onNavigateToForms(template.id)
                    },
                    label = template.name,
                    modifier = Modifier
                        .fillMaxWidth()

                )
            }
        }
    }
}