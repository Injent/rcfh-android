package ru.rcfh.blank.presentation.comparisontable.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.rcfh.designsystem.component.AppBackButton
import ru.rcfh.designsystem.component.AppIcon
import ru.rcfh.designsystem.component.AppIconButton
import ru.rcfh.designsystem.icon.Add
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Trash
import ru.rcfh.designsystem.theme.AppTheme

@Composable
internal fun TableTopBar(
    pageCount: Int,
    currentPage: Int,
    maxEntries: Int,
    onChangePage: (Int) -> Unit,
    onDeleteRequest: () -> Unit,
    onCreateRequest: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentPage) {
        listState.animateScrollToItem(
            index = (currentPage - 1).coerceAtLeast(0),
        )
    }

    Surface(
        color = AppTheme.colorScheme.background1,
        shape = CircleShape,
        modifier = modifier
            .height(54.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(AppTheme.spacing.xs)
        ) {
            AppBackButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(end = AppTheme.spacing.s)
            )
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.background2)
            ) {
                items(
                    count = pageCount
                ) { page ->
                    val selected = page == currentPage
                    val contentColor by animateColorAsState(
                        if (selected) AppTheme.colorScheme.foregroundOnBrand else AppTheme.colorScheme.foreground1
                    )
                    val containerColor by animateColorAsState(
                        if (selected) AppTheme.colorScheme.backgroundBrand else Color.Transparent
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(56.dp)
                            .height(48.dp)
                            .clip(CircleShape)
                            .clickable { onChangePage(page) }
                            .background(containerColor)
                    ) {
                        Text(
                            text = (page + 1).toString(),
                            style = AppTheme.typography.calloutButton,
                            color = contentColor,
                            maxLines = 1
                        )
                    }
                }
            }
            if (pageCount < maxEntries) {
                VerticalDivider(
                    color = AppTheme.colorScheme.stroke2,
                    modifier = Modifier
                        .padding(
                            horizontal = AppTheme.spacing.m,
                            vertical = AppTheme.spacing.s
                        )
                )
                AppIconButton(
                    containerColor = AppTheme.colorScheme.background2,
                    shape = CircleShape,
                    icon = AppIcon(
                        icon = AppIcons.Add,
                        onClick = onCreateRequest,
                        tint = Color.Black
                    ),
                    modifier = Modifier
                        .size(48.dp)
                )
            }

            if (pageCount > 1) {
                Spacer(Modifier.width(AppTheme.spacing.s))
                AppIconButton(
                    containerColor = AppTheme.colorScheme.paleRed,
                    shape = CircleShape,
                    icon = AppIcon(
                        icon = AppIcons.Trash,
                        onClick = onDeleteRequest,
                        tint = Color.Black
                    ),
                    modifier = Modifier
                        .size(48.dp)
                )
            }
        }
    }
}