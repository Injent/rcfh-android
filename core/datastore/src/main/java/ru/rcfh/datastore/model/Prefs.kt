package ru.rcfh.datastore.model

import kotlinx.serialization.Serializable
import ru.rcfh.core.model.UiTheme

@Serializable
data class Prefs(
    val uiTheme: UiTheme = UiTheme.LIGHT
)
