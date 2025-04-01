package ru.rcfh.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val dataVersion: DataVersion = DataVersion()
)