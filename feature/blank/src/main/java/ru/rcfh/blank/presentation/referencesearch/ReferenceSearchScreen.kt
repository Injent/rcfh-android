package ru.rcfh.blank.presentation.referencesearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.rcfh.blank.compose.bottom
import ru.rcfh.blank.presentation.referencesearch.component.ResultItem
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.query
import ru.rcfh.blank.ui.queryapi.update
import ru.rcfh.blank.ui.search.SearchLogic
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.data.model.ReferenceObj
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Search
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.feature.blank.R

@Serializable
data class ReferenceSearchScreen(
    val title: String,
    val handbookId: Int,
    val resultPath: String,
    val searchLogic: SearchLogic
) : NavKey

@Composable
internal fun ReferenceSearchRoute(
    state: Element,
    route: ReferenceSearchScreen,
    navigator: ViewerNavigator,
) {
    val viewModel = koinViewModel<ReferenceSearchViewModel> {
        parametersOf(route, state)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val alreadyHadSelectedOption = remember {
        state.query(route.resultPath) != null
    }
    val selectedOption by snapshotFlow {
        state.query(route.resultPath)
    }
        .mapNotNull { element ->
            (element?.encodeToJsonElement() as? JsonObject)?.let {
                ReferenceObj(it)
            }
        }
        .collectAsStateWithLifecycle(null)

    ReferenceSearchScreen(
        uiState = uiState,
        selectedOption = selectedOption,
        searchTextState = viewModel.searchTextState,
        title = route.title,
        onBack = navigator::goBack,
        onSelect = { reference ->
            val newState = Element.decodeFromJsonElement(reference.jsonObject, state.documentScope)
            state.update(route.resultPath, newState)
            navigator.goBack()
        },
        alreadyHadSelectedOption = alreadyHadSelectedOption
    )
}

@Composable
private fun ReferenceSearchScreen(
    uiState: ReferenceSearchUiState,
    selectedOption: ReferenceObj?,
    searchTextState: TextFieldState,
    title: String,
    onSelect: (ReferenceObj) -> Unit,
    onBack: () -> Unit,
    alreadyHadSelectedOption: Boolean,
) {
    println(alreadyHadSelectedOption)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(
                horizontal = AppTheme.spacing.l,
            )
            .padding(top = AppTheme.spacing.l)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BasicText(
                text = title,
                style = AppTheme.typography.headline1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = AppTheme.typography.footnote.fontSize,
                    maxFontSize = AppTheme.typography.headline1.fontSize,
                ),
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = AppTheme.spacing.l)
            )
            AppTextButton(
                text = stringResource(R.string.feature_blank_button_close),
                onClick = onBack,
                modifier = Modifier
                    .offset(x = AppTheme.spacing.l)
            )
        }

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
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        LaunchedEffect(Unit) {
            delay(300)
            focusRequester.requestFocus()
        }

        if (alreadyHadSelectedOption) {
            selectedOption?.let { option ->
                ResultItem(
                    text = option.name,
                    description = option.description,
                    onClick = onBack,
                    selected = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppTheme.spacing.s)
                )
                HorizontalDivider(
                    color = AppTheme.colorScheme.stroke2,
                    modifier = Modifier
                        .padding(horizontal = AppTheme.spacing.l)
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(
                top = AppTheme.spacing.s,
                bottom = WindowInsets.navigationBars.bottom
            ),
            modifier = Modifier
        ) {
            if (uiState.searchResults.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.feature_blank_info_nothingIsFound),
                            style = AppTheme.typography.callout,
                            color = AppTheme.colorScheme.foreground2
                        )
                    }
                }
            }
            items(
                items = uiState.searchResults,
            ) { result ->
                ResultItem(
                    text = result.name,
                    description = result.description,
                    onClick = { onSelect(result) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}