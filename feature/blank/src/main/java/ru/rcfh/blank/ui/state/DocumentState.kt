package ru.rcfh.blank.ui.state

import androidx.compose.runtime.mutableStateMapOf
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonElement
import ru.rcfh.blank.ui.component.Component
import ru.rcfh.blank.ui.component.RepeatableGroup
import ru.rcfh.blank.ui.component.TablePage
import ru.rcfh.blank.ui.preset.Row
import ru.rcfh.blank.ui.preset.initForm1Components
import ru.rcfh.blank.ui.preset.initForm2Components
import ru.rcfh.blank.ui.preset.initForm3Components
import ru.rcfh.blank.ui.preset.initForm4Components

interface DocumentScope {
    fun listen(collect: (Element) -> Unit)
    fun onChange()
    fun registerComponents(formId: Int, components: List<Component>)
    fun putComparisonTableComponents(tablePath: String, pages: List<TablePage>)
    fun putRepeatableComponents(repeatablePath: String, groups: List<RepeatableGroup>)
    fun putTableComponents(tablePath: String, rows: List<Row>)
    fun repeatableGroups(path: String): ImmutableList<RepeatableGroup>
}

class DocumentState(
    private val coroutineScope: CoroutineScope,
    initialData: JsonElement
) {
    private val mutex = Mutex()
    private var currentHashCode: Int = 0
    private val _changeEvent = MutableSharedFlow<Element>(replay = 1)
    val changeEvent = _changeEvent.asSharedFlow()

    val components = mutableStateMapOf<Int, ImmutableList<Component>>()
    val comparisonTableComponents = mutableStateMapOf<String, ImmutableList<TablePage>>()
    val repeatableComponents = mutableStateMapOf<String, ImmutableList<RepeatableGroup>>()
    val tableComponents = mutableStateMapOf<String, ImmutableList<Row>>()

    val documentScope = object : DocumentScope {
        override fun listen(collect: (Element) -> Unit) {
            coroutineScope.launch {
                changeEvent.collect { collect(it) }
            }
        }

        override fun onChange() {
            coroutineScope.launch {
                notifyChange()
            }
        }

        override fun registerComponents(formId: Int, components: List<Component>) {
            this@DocumentState.components[formId] = components.toImmutableList()
        }

        override fun putComparisonTableComponents(tablePath: String, pages: List<TablePage>) {
            comparisonTableComponents[tablePath] = pages.toImmutableList()
        }

        override fun putRepeatableComponents(repeatablePath: String, groups: List<RepeatableGroup>) {
            repeatableComponents[repeatablePath] = groups.toImmutableList()
        }

        override fun repeatableGroups(path: String): ImmutableList<RepeatableGroup> {
            return repeatableComponents[path] ?: persistentListOf()
        }

        override fun putTableComponents(tablePath: String, rows: List<Row>) {
            tableComponents[tablePath] = rows.toImmutableList()
        }
    }

    val state = Element.decodeFromJsonElement(
        jsonElement = initialData,
        documentScope = documentScope
    )

    init {
        initForm1Components(documentScope)
        initForm2Components(documentScope)
        initForm3Components(documentScope)
        initForm4Components(documentScope)
        coroutineScope.launch {
            _changeEvent.emit(state)
        }
    }

    private suspend fun notifyChange() {
        mutex.withLock {
            val newHashCode = state.contentHashCode()
            if (newHashCode == currentHashCode) return

            currentHashCode = newHashCode
            _changeEvent.emit(state)
        }
    }
}