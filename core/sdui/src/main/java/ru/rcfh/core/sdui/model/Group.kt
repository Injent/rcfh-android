package ru.rcfh.core.sdui.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val name: String,
    val forms: List<String>
)
