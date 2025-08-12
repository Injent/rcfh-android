package ru.rcfh.blank.ui.search

import ru.rcfh.blank.ui.state.Element
import ru.rcfh.data.model.ReferenceObj

fun interface FilterLogic {
    fun isMatching(state: Element, reference: ReferenceObj): Boolean
}

val FilterLogicPreset = mutableMapOf<String, FilterLogic>()