package ru.rcfh.feature.forms.presentation.sectioneditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.compose.LocalStateCache
import ru.rcfh.core.sdui.compose.ProvideStateStorage
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.component.TextInput
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.sectionEditor() {
    composable<Screen.SectionEditor> {
        SectionEditorRoute()
    }
}

@Composable
fun SectionEditorRoute() {
    val viewModel = koinViewModel<SectionEditorViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProvideStateStorage(
        cacheStorage = viewModel.cacheStorage,
        draftId = viewModel.route.draftId
    ) {
        when (uiState) {
            SectionEditorUiState.Loading -> {

            }
            is SectionEditorUiState.Error -> {
                Text(
                    text = "Ошибка в приложении",
                    style = AppTheme.typography.title3,
                    color = AppTheme.colorScheme.foregroundError
                )
            }
            is SectionEditorUiState.Success -> {
                SectionEditorScreen(
                    uiState = uiState as SectionEditorUiState.Success
                )
            }
        }
    }
}

@Composable
private fun SectionEditorScreen(
    uiState: SectionEditorUiState.Success
) {
    val coroutineScope = rememberCoroutineScope()
    val cache = LocalStateCache.current
    val pagesSize by cache.observeTableRowCount(parentTemplateId = uiState.template.id)
        .collectAsStateWithLifecycle(1)
    
    val pagerState = rememberPagerState { pagesSize }

    Scaffold(
        floatingActionButton = {
            if (uiState.template.repeatable) {
                Button(
                    onClick = {
                        coroutineScope.launch(NonCancellable) {
                            uiState.template.section.forEachIndexed { index, section ->
                                for (template in section.templates) {
                                    cache.putText(
                                        parentTemplateId = uiState.template.id,
                                        templateId = template.id,
                                        iteration = pagesSize,
                                        value = ""
                                    )
                                }
                            }
                            if (uiState.template.section
                                    .getOrNull(0)?.templates
                                    ?.getOrNull(0) == null) return@launch
                            // Write empty value for instant data refresh on screen
                            cache.putText(
                                parentTemplateId = uiState.template.id,
                                templateId = uiState.template.section[0].templates[0].id,
                                iteration = 0,
                                value = "",
                                instantWrite = true
                            )
                        }
                    },
                    shape = AppTheme.shapes.default,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AppTheme.colorScheme.backgroundBrand,
                        contentColor = AppTheme.colorScheme.foregroundOnBrand
                    ),
                    modifier = Modifier
                        .padding(bottom = AppTheme.spacing.l)
                ) {
                    Text(
                        text = "Добавить",
                        style = AppTheme.typography.calloutButton,
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppTheme.spacing.xxl)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagesSize) { page ->
                        Box(
                            Modifier
                                .background(
                                    color = if (page == pagerState.currentPage) {
                                        AppTheme.colorScheme.foreground
                                    } else AppTheme.colorScheme.foreground3,
                                    shape = CircleShape
                                )
                                .size(6.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            pageSpacing = AppTheme.spacing.l,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding()
                )
        ) { page ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                for (section in uiState.template.section) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
                        contentPadding = PaddingValues(
                            top = AppTheme.spacing.l,
                            bottom = innerPadding.calculateBottomPadding() + AppTheme.spacing.l
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        item {
                            Text(
                                text = section.name,
                                style = AppTheme.typography.headline1,
                                color = AppTheme.colorScheme.foreground1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(bottom = AppTheme.spacing.xl)
                            )
                        }
                        items(
                            items = section.templates
                        ) { temp ->
                            TextInput(
                                parentTemplateId = uiState.template.id,
                                template = temp,
                                iteration = page,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}