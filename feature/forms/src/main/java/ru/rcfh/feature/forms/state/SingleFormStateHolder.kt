package ru.rcfh.feature.forms.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.core.sdui.state.FormState
import ru.rcfh.core.sdui.storage.FormStateManager
import kotlin.reflect.KProperty

class SingleFormStateHolder<T : FormState> internal constructor(
    private val documentId: Int,
    private val formId: Int,
    private val template: Template,
    private val formStateManager: FormStateManager,
    private val scope: CoroutineScope,
) {
    private val state = mutableStateOf(template.createEmptyState())

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return state.value as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        scope.launch {
            formStateManager.saveState(documentId, formId, template.id, value)
        }
    }

    internal suspend fun load() {
        formStateManager.getStateFlow(
            documentId = documentId,
            formId = formId,
            template = template,
        ).collect { newState ->
            state.value = newState
        }
    }
}

@Composable
fun <T : FormState> formState(
    documentId: Int,
    formId: Int,
    template: Template
): SingleFormStateHolder<T> {
    val scope = rememberCoroutineScope()
    val formStateManager = koinInject<FormStateManager>()
    return remember(documentId, formId, template) {
        SingleFormStateHolder<T>(
            documentId = documentId,
            formId = formId,
            template = template,
            formStateManager = formStateManager,
            scope = scope
        ).apply {
            scope.launch {
                load()
            }
        }
    }
}