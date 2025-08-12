package ru.rcfh.blank.state

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.rcfh.navigation.Navigator

class ViewerNavigator(
    val backStack: SnapshotStateList<NavKey>,
    private val scope: CoroutineScope,
) {
    fun goTo(destination: NavKey) {
        backStack.add(destination)
    }

    fun goBack() {
        if (backStack.size == 1) {
            scope.launch {
                Navigator.navigateUp()
            }
        } else {
            backStack.removeLastOrNull()
        }
    }
}