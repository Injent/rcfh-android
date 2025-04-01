package ru.rcfh

import androidx.lifecycle.ViewModel
import ru.rcfh.data.util.SyncManager

class MainActivityViewModel(
    private val syncManager: SyncManager
) : ViewModel() {
    fun run() {
        syncManager.requestSync()
    }
}