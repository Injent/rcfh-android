package ru.rcfh.feature.forms.presentation.sectionrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.state.ComparisonTableState
import ru.rcfh.core.sdui.state.TextState
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.presentation.composables.TextComposable
import ru.rcfh.feature.forms.state.LocalSavedStateHandle
import ru.rcfh.feature.forms.state.formState
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.sectionRecordScreen() {
    composable<Screen.SectionRecord> {
        CompositionLocalProvider(
            LocalSavedStateHandle provides it.savedStateHandle
        ) {
            SectionRecordRoute()
        }
    }
}

@Composable
private fun SectionRecordRoute() {
    val viewModel = koinViewModel<SectionRecordViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SectionRecordUiState.Error -> Unit
        SectionRecordUiState.Loading -> Unit
        is SectionRecordUiState.Success -> {
            SectionRecordScreen(
                uiState = state
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectionRecordScreen(
    uiState: SectionRecordUiState.Success
) {
    var tableState by formState<ComparisonTableState>(
        documentId = uiState.documentId,
        formId = uiState.formId,
        template = uiState.tableTemplate
    )
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = uiState.initialPage,
        pageCount = { tableState.groups.size.coerceAtLeast(1) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.tableTemplate.name,
                        style = AppTheme.typography.title3,
                        color = AppTheme.colorScheme.foreground1
                    )
                }
            )
        },
        bottomBar = {
            if (uiState.tableTemplate.repeatable) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = AppTheme.spacing.l),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    items(
                        count = tableState.groups.size
                    ) { sectionIndex ->
                        AssistChip(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(sectionIndex)
                                }
                            },
                            label = {
                                Text(
                                    text = "Запись ${sectionIndex + 1}",
                                    style = AppTheme.typography.calloutButton,
                                    color = AppTheme.colorScheme.foreground1
                                )
                            }
                        )
                    }
                    item {
                        AssistChip(
                            onClick = {
                                val a = uiState.tableTemplate.firstSection.templates.map { TextState(id = it.id, value = "") }
                                val b = uiState.tableTemplate.secondSection.templates.map { TextState(id = it.id, value = "") }
                                tableState = tableState.copy(
                                    groups = tableState.groups + (a to b)
                                )
                            },
                            label = {
                                Text(
                                    text = "+",
                                    style = AppTheme.typography.calloutButton,
                                    color = AppTheme.colorScheme.foreground1
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.tableTemplate.repeatable) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { page ->
                val (firstSection, secondSection) = tableState.groups.getOrElse(page) {
                    val a = uiState.tableTemplate.firstSection.templates.map { TextState(id = it.id, value = "") }
                    val b = uiState.tableTemplate.secondSection.templates.map { TextState(id = it.id, value = "") }
                    tableState = tableState.copy(
                        groups = listOf(a to b)
                    )
                    a to b
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = AppTheme.spacing.l),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val rows = maxOf(
                        uiState.tableTemplate.firstSection.templates.size,
                        uiState.tableTemplate.secondSection.templates.size,
                    )

                    repeat(rows) { rowIndex ->
                        item {
                            if (rowIndex < uiState.tableTemplate.firstSection.templates.size) {
                                val template = uiState.tableTemplate.firstSection.templates[rowIndex]
                                val textState = firstSection.getOrNull(rowIndex) ?: template.createEmptyState()
                                TextComposable(
                                    value = textState.value,
                                    onValueChange = {
                                        val newGroups = tableState.groups.toMutableList()
                                        val newGroup = newGroups[page].first.toMutableList()
                                        while (newGroup.size <= rowIndex) {
                                            newGroup.add(null)
                                        }
                                        newGroup[rowIndex] = TextState(id = template.id, value = "")
                                        newGroups[page] = newGroup to newGroups[page].second
                                        tableState = tableState.copy(groups = newGroups)
                                    },
                                    template = template
                                )
                            } else {
                                Spacer(Modifier)
                            }
                        }

                        item {
                            if (rowIndex < uiState.tableTemplate.secondSection.templates.size) {
                                val template = uiState.tableTemplate.secondSection.templates[rowIndex]
                                val textState = secondSection.getOrNull(rowIndex) ?: template.createEmptyState()
                                TextComposable(
                                    value = textState.value,
                                    onValueChange = {
                                        val newGroups = tableState.groups.toMutableList()
                                        val newGroup = newGroups[page].second.toMutableList()
                                        while (newGroup.size <= rowIndex) {
                                            newGroup.add(null)
                                        }
                                        newGroup[rowIndex] = TextState(id = template.id, value = "")
                                        newGroups[page] = newGroups[page].first to newGroup
                                        tableState = tableState.copy(groups = newGroups)
                                    },
                                    template = template
                                )
                            } else {
                                Spacer(Modifier)
                            }
                        }
                    }
                }
            }
        } else {
            val (firstSection, secondSection) = tableState.groups.getOrElse(0) {
                val a = uiState.tableTemplate.firstSection.templates.map { TextState(id = it.id, value = "") }
                val b = uiState.tableTemplate.secondSection.templates.map { TextState(id = it.id, value = "") }
                tableState = tableState.copy(
                    groups = listOf(a to b)
                )
                a to b
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                contentPadding = PaddingValues(horizontal = AppTheme.spacing.l),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                val rows = maxOf(
                    uiState.tableTemplate.firstSection.templates.size,
                    uiState.tableTemplate.secondSection.templates.size
                )

                repeat(rows) { rowIndex ->
                    item {
                        if (rowIndex < uiState.tableTemplate.firstSection.templates.size) {
                            val template = uiState.tableTemplate.firstSection.templates[rowIndex]
                            val textState = firstSection.getOrNull(rowIndex) ?: template.createEmptyState()
                            TextComposable(
                                template = template,
                                value = textState.value,
                                onValueChange = { newText ->
                                    val newFirstSection = firstSection.toMutableList().apply {
                                        while (size <= rowIndex) {
                                            add(null)
                                        }
                                        this[rowIndex] = TextState(id = template.id, value = newText)
                                    }
                                    val newGroups = if (tableState.groups.isEmpty()) {
                                        listOf(newFirstSection to secondSection)
                                    } else {
                                        tableState.groups.toMutableList().apply { this[0] = newFirstSection to secondSection }
                                    }
                                    tableState = tableState.copy(groups = newGroups)
                                }
                            )
                        } else {
                            Spacer(Modifier)
                        }
                    }

                    item {
                        if (rowIndex < uiState.tableTemplate.secondSection.templates.size) {
                            val template = uiState.tableTemplate.secondSection.templates[rowIndex]
                            val textState = secondSection.getOrNull(rowIndex) ?: template.createEmptyState()
                            TextComposable(
                                template = template,
                                value = textState.value,
                                onValueChange = { newText ->
                                    val newSecondSection = secondSection.toMutableList().apply {
                                        while (size <= rowIndex) {
                                            add(null)
                                        }
                                        this[rowIndex] = TextState(id = template.id, value = newText)
                                    }
                                    val newGroups = if (tableState.groups.isEmpty()) {
                                        listOf(firstSection to newSecondSection)
                                    } else {
                                        tableState.groups.toMutableList().apply { this[0] = firstSection to newSecondSection }
                                    }
                                    tableState = tableState.copy(groups = newGroups)
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }
                }
            }
        }
    }
}