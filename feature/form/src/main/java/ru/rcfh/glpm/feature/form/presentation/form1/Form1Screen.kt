package ru.rcfh.glpm.feature.form.presentation.form1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import ru.rcfh.core.sdui.state.StateElement
import ru.rcfh.core.sdui.state.parse

@Composable
private fun Form1Route() {
    val state = rememberSaveable(saver = StateElement.Saver) { StateElement.parse("") }

    Form1Screen()
}

@Composable
private fun Form1Screen() {

}