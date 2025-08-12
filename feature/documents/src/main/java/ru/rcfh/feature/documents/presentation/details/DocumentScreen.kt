package ru.rcfh.feature.documents.presentation.details

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.component.AppCard
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.component.AppItemCard
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.component.SnackbarController
import ru.rcfh.designsystem.component.SnackbarMessage
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Check
import ru.rcfh.designsystem.icon.Cross
import ru.rcfh.designsystem.icon.DocumentSaved
import ru.rcfh.designsystem.icon.Edit
import ru.rcfh.designsystem.icon.StatusError
import ru.rcfh.designsystem.icon.StatusSuccess
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.documents.R
import ru.rcfh.glpm.ui.getFullName

@Composable
internal fun DocumentRoute(
    documentId: Int,
    isNew: Boolean,
    showBackButton: Boolean,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    BackHandler {
        onBack()
    }

    val viewModel = koinViewModel<DocumentViewModel>(key = documentId.toString()) {
        parametersOf(documentId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.detectErrors()
    }

    when (uiState) {
        DocumentUiState.Loading -> {}
        is DocumentUiState.Success -> {
            DocumentScreen(
                isNew = isNew,
                showBackButton = showBackButton,
                uiState = uiState as DocumentUiState.Success,
                onBack = onBack,
                onDeleteDocument = {
                    viewModel.deleteDocument {
                        SnackbarController.show(
                            SnackbarMessage(
                                text = context.getString(R.string.info_documentDeleted),
                                icon = AppIcons.StatusError,
                                tint = Color.Unspecified,
                            )
                        )
                        onBack()
                    }
                },
                onNavigateToSummarize = viewModel::navigateToSummarize,
                onNavigateToForm = viewModel::navigateToForm,
                onNameUpdate = viewModel::updateName,
                onExport = {
                    viewModel.exportFile(context) { uri ->
                        SnackbarController.show(
                            SnackbarMessage(
                                text = context.getString(R.string.info_fileSaved),
                                icon = AppIcons.StatusSuccess,
                                tint = Color.Unspecified,
                                actionLabel = context.getString(R.string.button_open),
                                onAction = {
                                },
                            )
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentScreen(
    showBackButton: Boolean,
    isNew: Boolean,
    uiState: DocumentUiState.Success,
    onNavigateToForm: (Int) -> Unit,
    onBack: () -> Unit,
    onDeleteDocument: () -> Unit,
    onNavigateToSummarize: () -> Unit,
    onNameUpdate: (String) -> Unit,
    onExport: () -> Unit,
) {
    val context = LocalContext.current
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (showBackButton) {
                        AppBackButton(
                            onClick = onBack,
                            modifier = Modifier
                                .offset(x = AppTheme.spacing.l)
                        )
                    }
                },
                actions = {
                    AppIconButton(
                        icon = AppIcon(
                            icon = AppIcons.DocumentSaved,
                            onClick = onExport
                        ),
                    )
                    AppTextButton(
                        text = stringResource(R.string.button_delete),
                        color = AppTheme.colorScheme.foregroundError,
                        onClick = onDeleteDocument
                    )
                },
            )
        },
    ) { innerPadding ->
        val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + AppTheme.spacing.l,
                bottom = innerPadding.calculateBottomPadding()
                    + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                + AppTheme.spacing.l
            ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .fillMaxHeight()
                .padding(horizontal = AppTheme.spacing.l)
        ) {
            item {
                val focusRequester = remember { FocusRequester() }
                var textFieldReadOnly by rememberSaveable { mutableStateOf(true) }
                val docName = rememberTextFieldState(uiState.document.name)

                fun completeAndUpdate() {
                    textFieldReadOnly = true
                    onNameUpdate(docName.text.toString())
                }

                AppTextField(
                    state = docName,
                    label = stringResource(R.string.placeholder_documentName),
                    placeholder = stringResource(R.string.placeholder_documentName),
                    error = if (docName.text.isBlank()) {
                        stringResource(R.string.error_emptyName)
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = true,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    onKeyboardAction = {
                        completeAndUpdate()
                    },
                    enabled = textFieldReadOnly,
                    button = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s)
                        ) {
                            if (textFieldReadOnly) {
                                AppIconButton(
                                    icon = AppIcon(
                                        icon = AppIcons.Edit,
                                        onClick = {
                                            textFieldReadOnly = false
                                            focusRequester.requestFocus()
                                        },
                                        tint = AppTheme.colorScheme.foreground1
                                    ),
                                    containerColor = AppTheme.colorScheme.background1
                                )
                            } else {
                                AppIconButton(
                                    icon = AppIcon(
                                        icon = AppIcons.Cross,
                                        onClick = {
                                            textFieldReadOnly = true
                                            docName.setTextAndPlaceCursorAtEnd(uiState.document.name)
                                        },
                                        tint = AppTheme.colorScheme.foreground1
                                    ),
                                    containerColor = AppTheme.colorScheme.background1
                                )
                                if (docName.text.isNotBlank()) {
                                    AppIconButton(
                                        icon = AppIcon(
                                            icon = AppIcons.Check,
                                            onClick = ::completeAndUpdate,
                                            tint = AppTheme.colorScheme.foregroundOnBrand
                                        ),
                                        containerColor = AppTheme.colorScheme.backgroundBrand
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .then(
                            when {
                                windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                                    Modifier.width(400.dp)
                                }
                                else -> Modifier.fillMaxWidth()
                            }
                        )
                        .padding(bottom = AppTheme.spacing.l)
                )

                LaunchedEffect(Unit) {
                    if (isNew) {
                        docName.clearText()
                        textFieldReadOnly = false
                        focusRequester.requestFocus()
                    }
                }
            }
            item {
                Box(Modifier.animateContentSize()) {
//                    uiState.errorReport?.let { report ->
//                        val (icon, headline, message) = when {
//                            report.severe.isNotEmpty() -> Triple(
//                                AppIcons.StatusError,
//                                stringResource(R.string.headline_severe),
//                                buildString {
//                                    append(
//                                        stringResource(
//                                            R.string.text_severe,
//                                            context.resources.getQuantityString(
//                                                R.plurals.fields,
//                                                report.severe.size,
//                                                report.severe.size
//                                            )
//                                        )
//                                    )
//
//                                    if (report.warnings.isNotEmpty()) {
//                                        appendLine()
//                                        append(
//                                            stringResource(
//                                                R.string.text_warning,
//                                                context.resources.getQuantityString(
//                                                    R.plurals.fields,
//                                                    report.warnings.size,
//                                                    report.warnings.size
//                                                )
//                                            )
//                                        )
//                                    }
//                                }
//                            )
//                            report.warnings.isNotEmpty() -> Triple(
//                                AppIcons.StatusWarning,
//                                stringResource(R.string.headline_warning),
//                                stringResource(
//                                    R.string.text_warning,
//                                    context.resources.getQuantityString(
//                                        R.plurals.fields,
//                                        report.warnings.size,
//                                        report.warnings.size
//                                    )
//                                )
//                            )
//                            else -> Triple(
//                                AppIcons.StatusSuccess,
//                                stringResource(R.string.headline_success),
//                                stringResource(R.string.text_success)
//                            )
//                        }
//                        MessageInfoBar(
//                            icon = icon,
//                            headline = headline,
//                            text = message,
//                            onClick = if (report.hasErrors) {
//                                { onNavigateToSummarize() }
//                            } else null,
//                            modifier = Modifier
//                                .animateItem()
//                        )
//                    } ?: run {
//                        MessageInfoBarSkeleton(
//                            modifier = Modifier
//                                .shimmer(shimmerInstance)
//                        )
//                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.subtitle_forms),
                    style = AppTheme.typography.subheadline,
                    color = AppTheme.colorScheme.foreground1
                )
            }
            items(
                items = uiState.formTabs
            ) { formTab ->
                AppItemCard(
                    onClick = {
                        onNavigateToForm(formTab.formId)
                    },
                    label = formTab.getFullName(context),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MessageInfoBar(
    icon: ImageVector,
    headline: String,
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    AppCard(
        onClick = onClick,
        contentPadding = PaddingValues(AppTheme.spacing.xl),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(40.dp)
            )
            Column {
                Text(
                    text = headline,
                    style = AppTheme.typography.headline2,
                    color = AppTheme.colorScheme.foreground1
                )
                Text(
                    text = text,
                    style = AppTheme.typography.callout,
                    color = AppTheme.colorScheme.foreground1,
                    modifier = Modifier
                        .padding(top = AppTheme.spacing.s)
                )

                onClick?.let {
                    Text(
                        text = stringResource(R.string.button_view),
                        style = AppTheme.typography.calloutButton,
                        color = AppTheme.colorScheme.link,
                        modifier = Modifier
                            .padding(top = AppTheme.spacing.xl)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageInfoBarSkeleton(
    modifier: Modifier = Modifier
) {
    AppCard(
        contentPadding = PaddingValues(AppTheme.spacing.xl),
        color = AppTheme.colorScheme.background4,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier
                .size(40.dp))
            Column {
                Text(
                    text = "",
                    style = AppTheme.typography.headline2,
                )
                Text(
                    text = "",
                    style = AppTheme.typography.callout,
                    modifier = Modifier
                        .padding(top = AppTheme.spacing.s)
                )

                Text(
                    text = "",
                    style = AppTheme.typography.calloutButton,
                    modifier = Modifier
                        .padding(top = AppTheme.spacing.xl)
                )
            }
        }
    }
}