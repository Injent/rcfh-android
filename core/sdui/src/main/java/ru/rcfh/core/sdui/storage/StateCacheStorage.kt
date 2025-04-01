package ru.rcfh.core.sdui.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.rcfh.database.dao.StateCacheDao
import ru.rcfh.database.entity.StateCacheEntity
import java.util.Collections
import kotlin.time.Duration.Companion.seconds

class StateCacheStorage(
    private val stateCacheDao: StateCacheDao
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val delayedUpdateJobs = mutableMapOf<String, Job>()
    private val cache = Collections.synchronizedMap(
        mutableMapOf<String, MutableMap<String, MutableList<MutableList<String>>>>()
    )

    suspend fun putState(
        draftId: String,
        path: String,
        value: String,
        index: Int = 0,
        innerIndex: Int = 0,
        instantWrite: Boolean = false
    ) {
        synchronized(cache) {
            runCatching {
                cache
                    .getOrPut(key = draftId) { mutableMapOf() }
                    .getOrPut(key = path) { mutableListOf() }
                    .let { list ->
                        list.getOrElse(index) { _ ->
                            mutableListOf<String>().also {
                                list.add(it)
                            }
                        }
                    }
                    .apply {
                        try {
                            set(innerIndex, value)
                        } catch (_: IndexOutOfBoundsException) {
                            add(value)
                        }
                    }
            }
                .onFailure { it.printStackTrace() }
        }

        val jobKey = draftId + path + index.toString() + innerIndex.toString()
        delayedUpdateJobs[jobKey]?.cancelAndJoin()
        delayedUpdateJobs[jobKey] = coroutineScope.launch {
            if (!instantWrite) {
                delay(1.seconds)
                ensureActive()
            }
            stateCacheDao.updateState(
                StateCacheEntity(
                    draftId = draftId,
                    path = path,
                    value = synchronized(cache) {
                        cache[draftId]!![path]!!
                    }
                )
            )
        }.apply {
            invokeOnCompletion {
                delayedUpdateJobs.remove(jobKey)
            }
        }
    }

    suspend fun removeIndexedState(
        draftId: String,
        path: String,
        index: Int
    ) {
        val paths = mutableListOf<String>()

        synchronized(cache) {
            cache[draftId]?.forEach { (key, list) ->
                if (key.startsWith(path)) {
                    paths += key
                    try {
                        list.removeAt(index)
                    } catch (_: IndexOutOfBoundsException) {}
                }
            }
        }

        paths.map { p ->
            StateCacheEntity(
                draftId = draftId,
                path = p,
                value = synchronized(cache) {
                    cache[draftId]?.get(p) ?: emptyList()
                }
            )
        }.let { stateCacheDao.insertAll(it) }
    }

    suspend fun removeInnerIndexedState(
        draftId: String,
        rootPath: String,
        index: Int,
        innerIndex: Int
    ) {
        val paths = mutableListOf<String>()

        synchronized(cache) {
            cache[draftId]?.forEach { (key, list) ->
                if (key.startsWith(rootPath)) {
                    paths += key
                    list.getOrNull(index)?.removeAt(innerIndex)
                }
            }
        }

        paths.map { p ->
            StateCacheEntity(
                draftId = draftId,
                path = p,
                value = cache[draftId]!![p]!!
            )
        }.let { stateCacheDao.insertAll(it) }
    }

    fun getState(draftId: String, path: String, index: Int = 0, innerIndex: Int = 0): String? {
        return synchronized(cache) {
            cache[draftId]?.get(path)?.getOrNull(index)?.getOrNull(innerIndex)
        }
    }

    fun observeState(draftId: String, path: String): Flow<StateCacheEntity?> {
        return stateCacheDao.observeState(draftId, path)
    }

    fun observeTable(draftId: String, path: String): Flow<List<StateCacheEntity>> {
        return stateCacheDao.observeTable(draftId, path)
    }

    suspend fun load() {
        if (cache.isNotEmpty()) return
        stateCacheDao.getAllStateCache()
            .groupBy(StateCacheEntity::draftId)
            .forEach { (draftId, entities) ->
                val map = mutableMapOf<String, MutableList<MutableList<String>>>()

                entities.forEach { entity ->
                    val mappedList = entity.value
                        .map { it.toMutableList() }
                        .toMutableList()
                    map[entity.path] = mappedList
                }
                cache[draftId] = map
            }
    }

    fun reset() {
        synchronized(cache) {
            cache.clear()
        }
    }
}