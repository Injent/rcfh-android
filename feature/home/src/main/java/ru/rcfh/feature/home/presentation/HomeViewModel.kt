package ru.rcfh.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.account.model.RcfhAccount
import ru.rcfh.core.account.repository.AccountRepository
import ru.rcfh.datastore.SettingsRepository
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

data class HomeUiState(
    val currentAccount: RcfhAccount? = null,
    val accounts: List<RcfhAccount> = emptyList()
)

class HomeViewModel(
    private val accountRepository: AccountRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState = combine(
        accountRepository.accountsFlow,
        settingsRepository.data.map { it.currentUserId }
    ) { accounts, currentUserId ->
        HomeUiState(
            accounts = accounts.sortedByDescending { it.userId == currentUserId },
            currentAccount = accounts.find { it.userId == currentUserId }
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun onChooseAccount(account: RcfhAccount) {
        viewModelScope.launch {
            accountRepository.choose(account)
        }
    }

    fun navigate(screen: Screen) {
        viewModelScope.launch {
            Navigator.navigate(screen)
        }
    }
}