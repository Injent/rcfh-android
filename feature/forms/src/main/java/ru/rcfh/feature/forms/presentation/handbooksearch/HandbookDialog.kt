package ru.rcfh.feature.forms.presentation.handbooksearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.core.sdui.compose.LocalStateCache
import ru.rcfh.core.sdui.compose.ProvideStateStorage
import ru.rcfh.data.model.Reference
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Search
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ClearFocusWithImeEffect
import ru.rcfh.designsystem.util.shadow.basicShadow
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.handbookDialog() {
    dialog<Screen.HandbookDialog> {
        HandbookDialogRoute()
    }
}

@Composable
private fun HandbookDialogRoute() {
    val viewModel = koinViewModel<HandbookDialogViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProvideStateStorage(
        cacheStorage = viewModel.cacheStorage,
        draftId = viewModel.route.draftId
    ) {
        val cache = LocalStateCache.current
        val coroutineScope = rememberCoroutineScope()

        HandbookDialogScreen(
            uiState = uiState,
            onSelect = { reference ->
                coroutineScope.launch {
                    val route = viewModel.route
                    cache.putText(
                        parentTemplateId = route.parentTemplateId,
                        templateId = route.templateId,
                        row = route.row,
                        iteration = route.iteration,
                        value = reference.name
                    )
                    Navigator.navigateUp()
                }
            },
            onBack = Navigator::navigateUp,
            searchTextState = viewModel.searchTextState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HandbookDialogScreen(
    uiState: HandbookDialogUiState,
    onSelect: (Reference) -> Unit,
    onBack: () -> Unit,
    searchTextState: TextFieldState,
) {
    BasicAlertDialog(
        onDismissRequest = onBack,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
        modifier = Modifier
            .imePadding()
            .widthIn(max = 600.dp)
            .background(AppTheme.colorScheme.background2, AppTheme.shapes.defaultTopCarved)
    ) {
        ClearFocusWithImeEffect()
        Scaffold(
            topBar = {
                val focusRequester = remember { FocusRequester() }
                AppTextField(
                    state = searchTextState,
                    placeholder = stringResource(android.R.string.search_go),
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    },
                    onCard = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicShadow()
                        .background(AppTheme.colorScheme.background1, AppTheme.shapes.default)
                        .padding(AppTheme.spacing.l)
                        .focusRequester(focusRequester)
                )

                LaunchedEffect(Unit) {
                    delay(300)
                    focusRequester.requestFocus()
                }
            }
        ) { innerPadding ->
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + AppTheme.spacing.l,
                    bottom = AppTheme.spacing.l
                ),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                uiState.results.forEachIndexed { index, reference ->
                    item(
                        key = index,
                    ) {
                        Text(
                            text = reference.name,
                            style = AppTheme.typography.callout,
                            minLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(reference)
                                }
                                .background(
                                    AppTheme.colorScheme.background1,
                                    when (index) {
                                        0 -> TopItemShape
                                        uiState.results.lastIndex -> BottomItemShape
                                        else -> AppTheme.shapes.small
                                    }
                                )
                                .padding(
                                    horizontal = AppTheme.spacing.m,
                                    vertical = AppTheme.spacing.s
                                )
                        )
                    }
                }
            }
        }
    }
}

private val TopItemShape = RoundedCornerShape(
    topStart = 16.dp, topEnd = 16.dp,
    bottomStart = 8.dp, bottomEnd = 8.dp
)
private val BottomItemShape = RoundedCornerShape(
    topStart = 8.dp, topEnd = 8.dp,
    bottomStart = 16.dp, bottomEnd = 16.dp
)