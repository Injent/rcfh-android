package ru.rcfh.core.sdui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import ru.rcfh.core.sdui.storage.StateCacheStorage
import ru.rcfh.core.sdui.storage.ScopedStateStorage

val LocalStateCache = compositionLocalOf<ScopedStateStorage> {
    error("TypedStateStorage not provided")
}

@Composable
fun ProvideStateStorage(
    cacheStorage: StateCacheStorage,
    draftId: String,
    row: Int? = null,
    content: @Composable () -> Unit
) {
    val storage = remember(draftId) {
        ScopedStateStorage(
            stateCacheStorage = cacheStorage,
            draftId = draftId
        )
    }
    CompositionLocalProvider(
        LocalStateCache provides storage,
        content = content
    )
}