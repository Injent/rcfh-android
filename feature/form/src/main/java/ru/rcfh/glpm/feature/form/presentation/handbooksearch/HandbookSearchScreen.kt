package ru.rcfh.glpm.feature.form.presentation.handbooksearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.data.model.Reference
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Check
import ru.rcfh.designsystem.icon.Search
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.feature.form.R
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.handbookSearchScreen() {
    composable<Screen.HandbookSearch> {
        HandbookSearchRoute(
            callbackId = it.toRoute<Screen.HandbookSearch>().callbackId,
        )
    }
}

@Composable
private fun HandbookSearchRoute(callbackId: String) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<HandbookSearchViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HandbookSearchScreen(
        uiState = uiState,
        onSelect = { reference ->
            viewModel.sendResult(reference.name)
        },
        onClose = {
            scope.launch {
                Navigator.navigateUp()
            }
        },
        searchTextState = viewModel.searchTextState
    )
}

@Composable
private fun HandbookSearchScreen(
    uiState: HandbookSearchUiState,
    onSelect: (Reference) -> Unit,
    onClose: () -> Unit,
    searchTextState: TextFieldState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
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
                text = uiState.title,
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
                text = stringResource(R.string.button_close),
                onClick = onClose,
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

        uiState.selectedOption?.let { option ->
            ResultItem(
                text = option,
                onClick = onClose,
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

        LazyColumn(
            contentPadding = PaddingValues(
                top = AppTheme.spacing.s
            ),
            modifier = Modifier
        ) {
            if (uiState.results.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.info_startTyping),
                            style = AppTheme.typography.callout,
                            color = AppTheme.colorScheme.foreground2
                        )
                    }
                }
            }
            items(
                items = uiState.results,
                key = { it.id }
            ) { result ->
                ResultItem(
                    text = result.name,
                    onClick = { onSelect(result) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ResultItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(AppTheme.shapes.default)
            .clickable { onClick() }
            .padding(
                horizontal = AppTheme.spacing.m,
                vertical = AppTheme.spacing.l
            )
    ) {
        Text(
            text = text,
            style = AppTheme.typography.callout,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
                .weight(1f)
                .padding(end = AppTheme.spacing.l)
        )

        if (selected) {
            Icon(
                imageVector = AppIcons.Check,
                contentDescription = null,
                tint = AppTheme.colorScheme.link,
                modifier = Modifier
                    .size(18.dp)
            )
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