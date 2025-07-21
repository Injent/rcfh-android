package ru.rcfh.core.sdui.event

import ru.rcfh.core.sdui.common.RefDependency

class SetReference(
    val callbackId: String,
    val templateId: String,
    val value: String,
    val refDependency: RefDependency?,
    val rowIndex: Int
)