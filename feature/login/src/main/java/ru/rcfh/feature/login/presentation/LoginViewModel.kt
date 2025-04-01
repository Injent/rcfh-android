package ru.rcfh.feature.login.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

class LoginViewModel(

) : ViewModel() {
    val loginState = TextFieldState("test")
    val passwordState = TextFieldState("test")

    fun onLogin() {
        viewModelScope.launch {
            Navigator.navigate(Screen.Templates)
        }
    }
}