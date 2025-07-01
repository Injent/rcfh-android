package ru.rcfh.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import ru.rcfh.common.now
import ru.rcfh.designsystem.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearPickerDialog(
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Surface(
            color = AppTheme.colorScheme.background1,
            shape = AppTheme.shapes.large
        ) {
            val years = remember { ((LocalDate.now().year - 100)..LocalDate.now().year).toList().reversed() }
            LazyVerticalGrid(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(AppTheme.spacing.xl),
                modifier = Modifier
                    .heightIn(max = 600.dp)
            ) {
                items(
                    items = years
                ) { year ->
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .clickable {
                                onSelect(year)
                            }
                            .background(
                                color = AppTheme.colorScheme.background2,
                                shape = AppTheme.shapes.small
                            )
                    ) {
                        Text(
                            text = year.toString(),
                            style = AppTheme.typography.calloutButton,
                            color = AppTheme.colorScheme.foreground1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}