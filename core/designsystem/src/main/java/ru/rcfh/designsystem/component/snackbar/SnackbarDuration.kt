package ru.rcfh.designsystem.component.snackbar

/**
 * Sealed interface representing different durations for a message.
 * This interface is used to define how long a message should be displayed.
 */
sealed interface SnackbarDuration {
    /**
     * The duration in milliseconds for which the message should be displayed.
     */
    val timeInMillis: Long

    /**
     * Represents a short duration for the message display.
     * The message will be displayed for 3000 milliseconds (3 seconds).
     */
    data object Short : SnackbarDuration {
        override val timeInMillis: Long = 3000
    }

    /**
     * Represents a permanent duration for the message display.
     * The message will be displayed indefinitely until manually dismissed.
     */
    data object Permanent : SnackbarDuration {
        override val timeInMillis: Long = Long.MAX_VALUE
    }

    /**
     * Represents a custom duration for the message display.
     * The duration is specified by the user and can be any positive long value.
     *
     * @property timeInMillis The duration in milliseconds for which the message should be displayed.
     */
    data class Custom(
        override val timeInMillis: Long,
    ) : SnackbarDuration
}
