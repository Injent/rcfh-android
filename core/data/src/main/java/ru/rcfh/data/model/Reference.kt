package ru.rcfh.data.model

data class Reference(
    val id: Int,
    val name: String,
    val description: String? = null,
    val signCodes: List<Int>? = null,
    val code: Int? = null
)
