package ru.rcfh.core.sdui.state

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import io.ktor.util.reflect.instanceOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import ru.rcfh.core.sdui.common.IndexAware
import ru.rcfh.core.sdui.common.PostInitListener
import ru.rcfh.core.sdui.template.FormId
import ru.rcfh.core.sdui.template.FormTemplate
import kotlin.reflect.KClass

class DocumentState(
    val documentId: Int,
    val documentScope: CoroutineScope,
    formTemplates: List<FormTemplate>,
    formsContent: Map<FormId, JsonObject>
) {
    var initialized = false
        private set
    private val events = MutableSharedFlow<Any>(replay = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val forms: SnapshotStateMap<FormId, FormState> =
        createFormsStates(
            documentState = this,
            formTemplates = formTemplates,
            formsContent = formsContent
        )

    init {
        initialized = true
        forms.forEach { (_, formState) ->
            formState.fields.forEach { it.notifyAboutInitialization() }
        }
    }

    inline fun <reified T : FieldState> findById(templateId: String, rowIndex: Int = -1): T? {
        if (!initialized) return null
        // Top level
        for (formState in forms.values) {
            formState.fields.find(templateId, rowIndex, type = T::class)
                ?.let { return it }
        }
        return null
    }

    fun findGroupById(templateId: String): List<RepeatableState> {
        val states = mutableListOf<RepeatableState>()
        for (formState in forms.values) {
            formState.fields.forEach { field ->
                if (field is TableState) {
                    field.rows.forEach { row ->
                        row.forEach { rowField ->
                            if (rowField.id == templateId && rowField is RepeatableState) {
                                states += rowField
                            }
                        }
                    }
                }
            }
        }
        return states
    }

    fun postEvent(event: Any) {
        if (!initialized) return
        documentScope.launch {
            events.emit(event)
        }
    }

    fun <T : Any> observeEvent(clazz: KClass<T>, onEvent: (T) -> Unit) {
        documentScope.launch {
            events
                .filterIsInstance(clazz)
                .collect { event ->
                    onEvent(event)
                }
        }
    }
}

fun <T : Any> List<FieldState>.find(id: String, rowIndex: Int, type: KClass<T>): T? {
    this.forEach { field ->
        if (field.id == id && field !is IndexAware && field.instanceOf(type)) return field as T

        if (field.instanceOf(type) && field is IndexAware && field.id == id && field.mIndex == rowIndex) return field as T

        if (field is Container) {
            field.items().find(id, rowIndex, type)?.let { return it }
        }
    }
    return null
}

inline fun <reified T> List<FieldState>.findState(id: String, rowIndex: Int): T? {
    val top = find {
        val matchIndex = if (it is IndexAware) {
            it.mIndex == rowIndex
        } else {
            true
        }
        it is T && it.id == id && matchIndex
    } as? T

    if (top != null) return top

    val containerTypes = arrayOf(
        RepeatableState::class,
        RatioState::class,
    )
    this
        .filter { fieldState ->
            containerTypes.any { it.isInstance(fieldState) }
        }
        .forEach { container ->
            when (container) {
                is RatioState -> {
                    container.values.find { it is T && it.id == id }?.let { return it as? T }
                }
                is RepeatableState -> {
                    for (group in container.groups) {
                        group.find { it is T && it.id == id }?.let { return it as? T }
                    }
                }
                else -> Unit
            }
        }
    return null
}

private fun createFormsStates(
    documentState: DocumentState,
    formTemplates: List<FormTemplate>,
    formsContent: Map<FormId, JsonObject>
): SnapshotStateMap<FormId, FormState> {
    val data = formTemplates
        .map { formTemp ->
            val a = formsContent.getOrDefault(formTemp.id, JsonNull)
            formTemp.id to FormState(
                id = formTemp.id,
                name = formTemp.name,
                fields = formTemp.templates
                    .map { template ->
                        template.restore(
                            documentState = documentState,
                            json = (a as? JsonObject)?.get(template.id) ?: JsonNull
                        )
                    }
                    .toImmutableList()
            )
        }
    return mutableStateMapOf<FormId, FormState>().apply { putAll(data) }
}

fun FieldState.notifyAboutInitialization() {
    when (this) {
        is Container -> {
            items().forEach {
                it.notifyAboutInitialization()
            }
        }
        is PostInitListener -> {
            onInitialized()
        }
        else -> Unit
    }
}