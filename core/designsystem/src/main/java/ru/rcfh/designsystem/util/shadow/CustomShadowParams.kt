package ru.rcfh.designsystem.util.shadow

import ru.rcfh.designsystem.util.shadow.Shadow

/**
 * Композитная тень дизайн системы
 *
 * @param name - название тени
 * @param layers - список теней
 */
data class CustomShadowParams(
    val name: String,
    val layers: List<Shadow>
)