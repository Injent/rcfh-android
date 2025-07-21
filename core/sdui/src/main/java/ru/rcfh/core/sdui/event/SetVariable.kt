package ru.rcfh.core.sdui.event

data class SetVariable(
    val templateId: String,
    val rowIndex: Int = -1,
    val value: String,
    val inGroup: String? = null
)