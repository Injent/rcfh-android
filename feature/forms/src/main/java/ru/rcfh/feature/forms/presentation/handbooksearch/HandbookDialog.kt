package ru.rcfh.feature.forms.presentation.handbooksearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.data.model.Reference
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Search
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.shadow.basicShadow
import ru.rcfh.feature.forms.R
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.handbookDialog(navController: NavController) {
    composable<Screen.HandbookSearch> {
        HandbookSearchRoute(
            resultKey = it.toRoute<Screen.HandbookSearch>().resultKey,
            navController = navController
        )
    }
}

@Composable
private fun HandbookSearchRoute(
    resultKey: String,
    navController: NavController
) {
    val viewModel = koinViewModel<HandbookSearchViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HandbookSearchScreen(
        uiState = uiState,
        onSelect = { reference ->
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set(resultKey, reference.name)
            navController.popBackStack()
        },
        searchTextState = viewModel.searchTextState
    )
}

@Composable
private fun HandbookSearchScreen(
    uiState: HandbookSearchUiState,
    onSelect: (Reference) -> Unit,
    searchTextState: TextFieldState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
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
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
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

        LazyColumn(
            contentPadding = PaddingValues(AppTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs),
            modifier = Modifier
        ) {
            if (uiState.results.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.info_nothingFound),
                            style = AppTheme.typography.callout,
                            color = AppTheme.colorScheme.foreground2
                        )
                    }
                }
            }
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

private val TopItemShape = RoundedCornerShape(
    topStart = 16.dp, topEnd = 16.dp,
    bottomStart = 8.dp, bottomEnd = 8.dp
)
private val BottomItemShape = RoundedCornerShape(
    topStart = 8.dp, topEnd = 8.dp,
    bottomStart = 16.dp, bottomEnd = 16.dp
)