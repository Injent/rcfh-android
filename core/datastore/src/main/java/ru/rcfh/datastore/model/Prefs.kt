package ru.rcfh.datastore.model

import kotlinx.serialization.Serializable
import ru.rcfh.core.model.DarkThemeConfig

@Serializable
data class Prefs(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.LIGHT
)
