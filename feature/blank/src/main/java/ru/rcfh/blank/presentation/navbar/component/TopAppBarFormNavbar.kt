package ru.rcfh.blank.presentation.navbar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.rcfh.core.model.FormTab
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.shadow.basicShadow

@Composable
internal fun TopAppBarFormNavbar(
    selectedTab: FormTab.Tab,
    formTabs: ImmutableList<FormTab>,
    onSelect: (id: Int) -> Unit,
    onReplace: (oldId: Int, newId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppTheme.colorScheme.background1,
        shape = AppTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .basicShadow(AppTheme.shapes.large)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(AppTheme.spacing.s)
        ) {
            formTabs.forEach { tab ->
                FormNavbarItem(
                    tab = tab,
                    selected = tab == selectedTab,
                    onSelect = onSelect,
                    onReplace = onReplace,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
            }
        }
    }
}