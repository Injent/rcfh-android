package ru.rcfh.feature.forms.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.compose.LocalStateCache
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.designsystem.component.Table
import ru.rcfh.designsystem.theme.AppTheme

@Composable
internal fun TableInput(
    template: Template.Table,
    onEditRequest: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val cache = LocalStateCache.current
    val coroutineScope = rememberCoroutineScope()
    val rowCount by cache.observeTableRowCount(template.id).collectAsStateWithLifecycle(initialValue = 0)
    val repeatablesWidth = remember { mutableStateMapOf<Pair<String, Int>, Int>() }

    Column(modifier = modifier) {
        Text(
            text = template.name,
            style = AppTheme.typography.headline1,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
                .padding(bottom = AppTheme.spacing.m)
        )
        val horizontalScrollState = rememberScrollState()
        val startGradientColor = AppTheme.colorScheme.background2

        Table(
            verticalScrollEnabled = false,
            horizontalScrollState = horizontalScrollState,
            rowCount = rowCount + 1,
            columnCount = template.columns.size + 1,
            modifier = Modifier
                .drawWithCache {
                    val gradientWidthPx = 56.dp.toPx()
                    val startGradientBrush = Brush.horizontalGradient(
                        colors = listOf(startGradientColor, Color.Transparent),
                        startX = 0f,
                        endX = gradientWidthPx
                    )
                    val endGradientBrush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, startGradientColor),
                        startX = size.width - gradientWidthPx,
                        endX = size.width
                    )

                    onDrawWithContent {
                        drawContent()
                        if (horizontalScrollState.value > 0) {
                            drawRect(
                                brush = startGradientBrush,
                                size = Size(
                                    width = gradientWidthPx,
                                    height = size.height
                                )
                            )
                        }
                        if (horizontalScrollState.value < horizontalScrollState.maxValue) {
                            drawRect(
                                brush = endGradientBrush,
                                topLeft = Offset(x = size.width - gradientWidthPx, y = 0f),
                                size = Size(
                                    width = gradientWidthPx,
                                    height = size.height
                                )
                            )
                        }
                    }
                }
                .clip(AppTheme.shapes.default)
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = AppTheme.shapes.default
                )
        ) { row, column ->
            if (column == 0) {
                if (row == 0) {
                    Surface(
                        color = HeaderCellColor
                    ) { }
                } else {
                    IconButton(
                        onClick = {
                            coroutineScope.launch(NonCancellable) {
                                cache.removeRow(
                                    parentTemplateId = template.id,
                                    row = row - 1
                                )
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = AppTheme.colorScheme.foregroundError
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (row == 0 && column > 0) {
                when (val temp = template.columns[column - 1]) {
                    is Template.Repeatable -> {
                        val repeatableSize by cache.observeIterationCount(
                            templateId = temp.id,
                            row = 0
                        ).collectAsStateWithLifecycle(0)

                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Max)
                        ) {
                            repeat(repeatableSize) { iteration ->
                                HeaderCell(
                                    text = "${temp.name} ${iteration + 1}",
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .heightIn(max = 50.dp)
                                        .width(with(density) {
                                            repeatablesWidth.getOrDefault(temp.id to iteration, 320).toDp()
                                        })
                                )
                            }
                        }
                    }
                    is Template.Text -> {
                        HeaderCell(
                            text = temp.name,
                            modifier = Modifier
                                .widthIn(max = 320.dp)
                        )
                    }
                    else -> Unit
                }
            } else if (column > 0) {
                when (val parentTemplate = template.columns[column - 1]) {
                    is Template.Repeatable -> {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Max)
                        ) {
                            val repeatableSize by cache.observeIterationCount(
                                templateId = parentTemplate.id,
                                row = row - 1
                            ).collectAsStateWithLifecycle(0)

                            repeat(repeatableSize) { iteration ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .height(IntrinsicSize.Max)
                                        .onSizeChanged { size ->
                                            repeatablesWidth[parentTemplate.id to iteration] = size.width
                                        }
                                ) {
                                    parentTemplate.templates.forEach { temp ->
                                        val cellValue by cache.observeText(
                                            parentTemplateId = parentTemplate.id,
                                            templateId = temp.id,
                                            row = row - 1,
                                            iteration = iteration
                                        ).collectAsStateWithLifecycle(initialValue = "")

                                        ContentCell(
                                            text = cellValue,
                                            onClick = {
                                                onEditRequest(row - 1)
                                            },
                                            modifier = Modifier
                                                .fillMaxHeight()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is Template.Text -> {
                        val cellValue by cache.observeText(
                            parentTemplateId = template.id,
                            templateId = parentTemplate.id,
                            row = row - 1,
                        ).collectAsStateWithLifecycle(initialValue = "")

                        ContentCell(
                            text = cellValue,
                            onClick = {
                                onEditRequest(row - 1)
                            }
                        )
                    }
                    else -> Unit
                }
            }
        }
        Button(
            onClick = {
                onEditRequest(rowCount)
            },
            shape = AppTheme.shapes.default,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = AppTheme.colorScheme.backgroundBrand,
                contentColor = AppTheme.colorScheme.foregroundOnBrand
            ),
            modifier = Modifier
        ) {
            Text(
                text = "Добавить",
                style = AppTheme.typography.calloutButton,
            )
        }
    }
}

@Composable
fun HeaderCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(HeaderCellColor)
    ) {
        val subtitleColor = AppTheme.colorScheme.foreground2
        Text(
            text = remember(text) {
                buildAnnotatedString {
                    addStyle(
                        style = SpanStyle(fontSize = 12.sp),
                        start = text.indexOfFirst { it == '(' }.takeIf { it != -1 } ?: 0,
                        end = text.indexOfLast { it == ')' }.takeIf { it != -1 }?.plus(1) ?: 0
                    )

                    if (text.count { it == '\n' } == 1) {
                        val (title, subtitle) = text.split('\n')

                        append(title)
                        withStyle(
                            SpanStyle(
                                color = subtitleColor,
                                fontSize = 12.sp
                            )
                        ) {
                            appendLine()
                            append(subtitle)
                        }
                    } else {
                        append(text)
                    }
                }
            },
            style = AppTheme.typography.headline2
                .copy(lineHeight = 14.sp),
            color = AppTheme.colorScheme.foreground1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(AppTheme.spacing.m)
                .align(Alignment.Center)
        )
        Surface(
            color = BorderColor,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(2.dp)
                .height(20.dp)
        ) {}
    }
}

@Composable
fun ContentCell(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .widthIn(max = 320.dp)
            .background(AppTheme.colorScheme.background1)
            .border(width = 1.dp, color = BorderColor)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = AppTheme.colorScheme.foreground1,
            style = AppTheme.typography.callout,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Center)
        )
    }
}

private val HeaderCellColor = Color(0xFFfafafb)
private val BorderColor = Color(0xFFd9d9db)