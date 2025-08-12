package ru.rcfh.blank.presentation.navbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import ru.rcfh.blank.presentation.navbar.component.TopAppBarFormNavbar
import ru.rcfh.core.model.FormTab

@Composable
fun NavbarComposable(
    selectedTab: FormTab.Tab,
    formTabs: ImmutableList<FormTab>,
    onSelect: (id: Int) -> Unit,
    onReplace: (oldId: Int, newId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBarFormNavbar(
        selectedTab = selectedTab,
        formTabs = formTabs,
        onSelect = onSelect,
        onReplace = onReplace,
        modifier = modifier
    )
}