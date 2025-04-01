package ru.rcfh.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class DataVersion(
    val handbooks: Int = 0
)