package ru.rcfh.blank.presentation.comparisontable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.rcfh.blank.compose.bottom
import ru.rcfh.blank.compose.top
import ru.rcfh.blank.presentation.comparisontable.component.SwitchSourceCard
import ru.rcfh.blank.presentation.comparisontable.component.TabWithCheck
import ru.rcfh.blank.presentation.comparisontable.component.TableTopBar
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.component.TablePage
import ru.rcfh.blank.ui.queryapi.query
import ru.rcfh.blank.ui.queryapi.queryOrCreate
import ru.rcfh.blank.ui.queryapi.update
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.blank.ui.state.ObjectElement
import ru.rcfh.blank.ui.state.create
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.feature.blank.R

@Serializable
data class ComparisonTableScreen(
    val tableRootPath: String,
    val maxEntries: Int
) : NavKey

@Composable
internal fun ComparisonTableRoute(
    documentScope: DocumentScope,
    tableRootPath: String,
    maxEntries: Int,
    pages: ImmutableList<TablePage>,
    state: Element,
    navigator: ViewerNavigator
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { pages.size.coerceAtLeast(1) }

    Scaffold(
        topBar = {
            if (maxEntries > 1) {
                TableTopBar(
                    pageCount = pagerState.pageCount,
                    currentPage = pagerState.currentPage,
                    maxEntries = 5,
                    onChangePage = { page ->
                        scope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    },
                    onDeleteRequest = {
                        state.query("${tableRootPath}.rows")?.array?.removeAt(pagerState.currentPage)
                    },
                    onCreateRequest = {
                        val array = state.query("${tableRootPath}.rows")?.array ?: return@TableTopBar
                        array.add(state.create<ObjectElement>())

                        scope.launch {
                            delay(300) // Waiting for component reordering
                            pagerState.animateScrollToPage(
                                page = array.size
                            )
                        }
                    },
                    onBack = navigator::goBack,
                    modifier = Modifier
                        .padding(horizontal = AppTheme.spacing.l)
                )
            } else {
                AppBackButton(
                    onClick = navigator::goBack,
                    modifier = Modifier
                        .padding(
                            start = AppTheme.spacing.l,
                            top = AppTheme.spacing.l
                        )
                )
            }
        },
        modifier = Modifier
            .statusBarsPadding()
            .imePadding()
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(top = AppTheme.spacing.l),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.top)
        ) { page ->
            val useActual = state.queryOrCreate("${tableRootPath}.rows[$page].useActual", false)
            var selectedTab by rememberSaveable { mutableIntStateOf(if (useActual) 1 else 0) }

            fun setUseActual(useActual: Boolean) {
                state.update("${tableRootPath}.rows[$page].useActual", useActual.toString())
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TabWithCheck(
                        name = stringResource(R.string.feature_blank_tab_origin),
                        checked = !useActual,
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabWithCheck(
                        name = stringResource(R.string.feature_blank_tab_actual),
                        checked = useActual,
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                AnimatedVisibility(
                    visible = selectedTab == 0 && useActual,
                ) {
                    SwitchSourceCard(
                        name = stringResource(
                            R.string.feature_blank_info_switchSource,
                            stringResource(R.string.feature_blank_tab_origin)
                        ),
                        onClick = { setUseActual(false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppTheme.spacing.s)
                    )
                }
                AnimatedVisibility(
                    visible = selectedTab == 1 && !useActual,
                ) {
                    SwitchSourceCard(
                        name = stringResource(
                            R.string.feature_blank_info_switchSource,
                            stringResource(R.string.feature_blank_tab_actual)
                        ),
                        onClick = { setUseActual(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppTheme.spacing.s)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    contentPadding = PaddingValues(
                        top = AppTheme.spacing.l,
                        bottom = innerPadding.bottom + AppTheme.spacing.l
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(
                        items = if (selectedTab == 1) {
                            pages.getOrNull(page)?.actual ?: emptyList()
                        } else {
                            pages.getOrNull(page)?.origin ?: emptyList()
                        }
                    ) { component ->
                        component.Content(
                            state = state,
                            navigator = navigator,
                            documentScope = documentScope
                        )
                    }
                }
            }
        }
    }
}