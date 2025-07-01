package ru.rcfh.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.thenIf

@Composable
fun HeaderCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(AppTheme.colorScheme.tableHeader)
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
            color = AppTheme.colorScheme.stroke2,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(2.dp)
                .height(20.dp)
        ) {}
    }
}

@Composable
fun RowCell(
    text: String,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .background(HeaderCellColor)
            .padding(AppTheme.spacing.m)
    )
}

@Composable
fun ContentCell(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .widthIn(max = 320.dp)
            .background(AppTheme.colorScheme.background1)
            .border(width = 1.dp, color = BorderColor)
            .clickable { onClick() }
    ) {
        content()
    }
}

@Composable
fun ContentCell(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customStyle: Boolean = false,
) {
    Box(
        modifier = modifier
            .widthIn(max = 320.dp)
            .thenIf(!customStyle) {
                background(AppTheme.colorScheme.background1)
            }
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

val HeaderCellColor = Color(0xFFfafafb)
private val BorderColor = Color(0xFFd9d9db)