package ru.rcfh.datastore.model

import arrow.optics.optics
import kotlinx.serialization.Serializable

@Serializable
@optics
data class CachedForm(
    val templateId: Int,
    val data: List<CachedData> = emptyList(),
) {
    companion object
}

fun CachedForm.findField(field: String): CachedData.Plain? {
    return data.filterIsInstance<CachedData.Plain>().find { it.field == field }
}

fun CachedForm.findTable(tableId: Int): CachedData.Table? {
    return data.filterIsInstance<CachedData.Table>().find { it.tableId == tableId }
}