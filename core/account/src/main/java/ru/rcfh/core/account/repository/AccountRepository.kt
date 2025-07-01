package ru.rcfh.core.account.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pro.respawn.apiresult.ApiResult
import ru.rcfh.core.account.model.RcfhAccount
import ru.rcfh.core.account.model.SignInForm
import ru.rcfh.core.account.util.RcfhAccountManager
import ru.rcfh.datastore.SettingsRepository
import ru.rcfh.network.ktor.KtorService

class AccountRepository(
    private val ktorService: KtorService,
    private val settingsRepository: SettingsRepository,
    private val accountManager: RcfhAccountManager
) {
    val currentAccount: Flow<RcfhAccount?> = settingsRepository.data.map {
        it.currentUserId?.let { userId ->
            accountManager.getAccounts().find { it.userId == userId }
        }
    }
    val accountsFlow = accountManager.accounts

    suspend fun choose(account: RcfhAccount) {
        settingsRepository.setCurrentUserId(account.userId)
    }

    suspend fun signIn(form: SignInForm): ApiResult<Unit> = ApiResult {
        settingsRepository.setCurrentUserId(form.login.hashCode())
        accountManager.upsertAccount(
            RcfhAccount(
                login = form.login,
                displayName = form.login,
                userId = form.login.hashCode()
            )
        )
    }
}