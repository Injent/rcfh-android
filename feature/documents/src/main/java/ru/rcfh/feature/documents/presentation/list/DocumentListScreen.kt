package ru.rcfh.feature.documents.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.toJavaLocalDateTime
import ru.rcfh.core.sdui.model.Document
import ru.rcfh.designsystem.component.AppSnackbarHost
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Menu
import ru.rcfh.designsystem.icon.StatusError
import ru.rcfh.designsystem.icon.StatusSuccess
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.ListenEvents
import ru.rcfh.feature.documents.R
import ru.rcfh.feature.documents.presentation.composables.CardWithStatus
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun DocumentListRoute(
    viewModel: DocumentListViewModel,
    onSelectDocument: (Int) -> Unit,
    onMenuClick: () -> Unit
) {
    ListenEvents(viewModel.events) { event ->
        when (event) {
            is DocumentListEvent.DocumentCreated -> onSelectDocument(event.documentId)
        }
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DocumentListScreen(
        onSelectDocument = onSelectDocument,
        onCreateDocument = viewModel::onCreateDocument,
        onBack = viewModel::back,
        onMenuClick = onMenuClick,
        uiState = uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentListScreen(
    onSelectDocument: (Int) -> Unit,
    onCreateDocument: (name: String) -> Unit,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    uiState: DocumentListUiState
) {
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(AppTheme.colorScheme.background2)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(TopAppBarDefaults.MediumAppBarCollapsedHeight)
            ) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .padding(start = AppTheme.spacing.xs)
                ) {
                    Icon(
                        imageVector = AppIcons.Menu,
                        contentDescription = null,
                        tint = AppTheme.colorScheme.foreground1,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }

                val textColor = AppTheme.colorScheme.foreground1
                BasicText(
                    text = stringResource(R.string.title_documents),
                    style = AppTheme.typography.title3,
                    color = { textColor },
                    autoSize = TextAutoSize.StepBased(
                        maxFontSize = AppTheme.typography.title3.fontSize
                    ),
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = AppTheme.spacing.xs)
                )

                Spacer(Modifier.weight(1f))

                AppTextButton(
                    text = stringResource(R.string.button_createDocument),
                    onClick = {
                        onCreateDocument(context.getString(R.string.unnamedDocumentName))
                    },
                    modifier = Modifier
                )
            }
        },
        snackbarHost = {
            AppSnackbarHost(
                modifier = Modifier
                    .navigationBarsPadding()
            )
        },
    ) { innerPadding ->
        if (uiState.documents.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(
                        color = AppTheme.colorScheme.foreground
                    )
                } else {
                    Text(
                        text = stringResource(R.string.placeholder_noDocuments),
                        style = AppTheme.typography.body,
                        color = AppTheme.colorScheme.foreground2
                    )
                }
            }
        } else {
            var selectedDocumentId by rememberSaveable { mutableIntStateOf(-1) }

            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                            + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                            + AppTheme.spacing.l
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppTheme.spacing.l)
            ) {
                documents(
                    uiState = uiState,
                    onSelect = { documentId ->
                        onSelectDocument(documentId)
                        selectedDocumentId = documentId
                    },
                    selectedDocumentId = selectedDocumentId
                )
            }
        }
    }
}

private fun LazyListScope.documents(
    uiState: DocumentListUiState,
    onSelect: (Int) -> Unit,
    selectedDocumentId: Int,
) {
    itemsIndexed(
        items = uiState.documents
    ) { index, document ->
        val dividerColor = AppTheme.colorScheme.background2
        val dividerWidthPx = LocalDensity.current.run { 2.dp.toPx() }

        DocumentItem(
            document = document,
            onClick = {
                onSelect(document.id)
            },
            selected = selectedDocumentId == document.id,
            loading = uiState.loading,
            shape = when (index) {
                0 -> if (uiState.documents.size == 1) {
                    AppTheme.shapes.default
                } else {
                    AppTheme.shapes.defaultTopCarved
                }
                uiState.documents.lastIndex -> AppTheme.shapes.defaultBottomCarved
                else -> RectangleShape
            },
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    if (index != 0) {
                        drawLine(
                            color = dividerColor,
                            start = Offset.Zero,
                            end = Offset(x = size.width, y = 0f),
                            strokeWidth = dividerWidthPx
                        )
                    }
                }
        )
    }
}

@Composable
private fun DocumentItem(
    document: Document,
    onClick: () -> Unit,
    loading: Boolean,
    selected: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    CardWithStatus(
        modifier = modifier,
        headline = document.name,
        text = remember(document) {
            DefaultDateTimeFormatter.format(
                document.modificationTimestamp.toJavaLocalDateTime()
            )
        },
        onClick = onClick,
        shape = shape,
        selected = selected,
        trailingIcon = {
            if (loading) {
                CircularProgressIndicator(
                    color = AppTheme.colorScheme.foreground3,
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .size(20.dp)
                )
            } else {
                Icon(
                    imageVector = if (document.isValid) {
                        AppIcons.StatusSuccess
                    } else {
                        AppIcons.StatusError
                    },
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }
    )
}

private val DefaultDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(
    FormatStyle.SHORT, FormatStyle.SHORT
)