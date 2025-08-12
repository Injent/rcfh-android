package ru.rcfh.blank.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

val PaddingValues.top: Dp
    get() = calculateTopPadding()

val PaddingValues.bottom: Dp
    get() = calculateBottomPadding()

val PaddingValues.start: Dp
    get() = calculateLeftPadding(LayoutDirection.Ltr)

val PaddingValues.end: Dp
    get() = calculateRightPadding(LayoutDirection.Ltr)

val WindowInsets.top: Dp
    @Composable get() = LocalDensity.current.run {
        getTop(this).toDp()
    }

val WindowInsets.bottom: Dp
    @Composable get() = LocalDensity.current.run {
        getBottom(this).toDp()
    }

val WindowInsets.start: Dp
    @Composable get() = LocalDensity.current.run {
        getLeft(this, LayoutDirection.Ltr).toDp()
    }

val WindowInsets.end: Dp
    @Composable get() = LocalDensity.current.run {
        getRight(this, LayoutDirection.Ltr).toDp()
    }