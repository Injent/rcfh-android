package ru.rcfh.feature.login.presentation.signup

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.signUpScreen() {
    composable<Screen.SignUp> {
        SignUpRoute()
    }
}

@Composable
private fun SignUpRoute() {
    val viewModel = koinViewModel<SignUpViewModel>()

    SignUpScreen()
}

@Composable
private fun SignUpScreen() {

}