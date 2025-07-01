package ru.rcfh.feature.login.presentation.signin

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pro.respawn.apiresult.onSuccess
import ru.rcfh.core.account.model.SignInForm
import ru.rcfh.core.account.repository.AccountRepository
import ru.rcfh.data.util.SyncManager
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

class SignInViewModel(
    private val accountRepository: AccountRepository,
    private val syncManager: SyncManager,
) : ViewModel() {
    val loginState = TextFieldState("test")
    val passwordState = TextFieldState("test")

    fun onLogin() {
        viewModelScope.launch {
            accountRepository.signIn(
                SignInForm(
                    login = loginState.text.toString().trim(),
                    password = passwordState.text.toString().trim()
                )
            )
                .onSuccess {
                    syncManager.requestSync()
                    Navigator.navigate(Screen.Documents()) {
                        popUpTo(0)
                    }
                }
        }
    }
}