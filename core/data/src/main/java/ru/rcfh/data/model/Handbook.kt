package ru.rcfh.data.model

data class Handbook(
    val id: Int,
    val name: String,
    val references: List<ReferenceObj>
)
