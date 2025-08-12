package ru.rcfh.designsystem.component.snackbar

/**
 * Interface representing the content of a snackbar message.
 * It encapsulates both the content to be displayed and the duration for which it should be shown.
 *
 * @param T The type of content to be displayed in the snackbar.
 */
interface SnackbarContent<T> {
    /**
     * The duration for which the snackbar should be displayed.
     * This can be a predefined duration like [SnackbarDuration.Short] or a custom duration.
     */
    val duration: SnackbarDuration

    /**
     * The actual content to be displayed in the snackbar.
     * This can be any type T, which will be rendered by the snackbar content composable.
     */
    val content: T
}
