package ru.rcfh.blank.ui.search

import ru.rcfh.data.model.ReferenceObj

enum class SearchLogic(
    private val searchImpl: (String, List<ReferenceObj>) -> List<ReferenceObj>
) {
    BASIC({ query, items ->
        items.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description?.contains(query, ignoreCase = true) == true
        }
            .sortedBy {
                when {
                    it.name.startsWith(query, ignoreCase = true) -> 0
                    it.name.contains(query, ignoreCase = true) -> 1
                    else -> 2
                }
            }
    }),
    SPECIES({ query, items ->
        items.filter { item ->
            item.name.lowercase().startsWith(query.lowercase()) ||
                    item.description?.lowercase()?.startsWith(query.lowercase()) ?: false ||
                    item.description?.lowercase()?.contains(query.lowercase()) ?: false
        }.sortedByDescending { item ->
            when {
                item.name.lowercase().startsWith(query.lowercase()) -> 3
                item.description?.lowercase()?.startsWith(query.lowercase()) == true -> 2
                item.description?.lowercase()?.contains(query.lowercase()) == true -> 1
                else -> 0
            }
        }
    });

    fun search(query: String, items: List<ReferenceObj>): List<ReferenceObj> =
        searchImpl(query, items)
}