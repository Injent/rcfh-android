package ru.rcfh.core.account.util

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.rcfh.core.account.model.RcfhAccount
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RcfhAccountManager(context: Context) {
    private val accountManager by lazy { AccountManager.get(context) }
    private val _accounts = MutableStateFlow<List<RcfhAccount>>(emptyList())
    val accounts: Flow<List<RcfhAccount>> = _accounts.asStateFlow()
        .onStart {
            _accounts.value = getAccounts()
        }

    suspend fun upsertAccount(account: RcfhAccount) {
        if (getAccount(account.login) != null) {
            updateAccount(account)
        } else {
            createAccount(account)
        }
        refreshAccountsFlow()
    }

    suspend fun getAccounts(): List<RcfhAccount> = withContext(Dispatchers.IO) {
        getAllAccounts().map { account ->
            RcfhAccount(
                login = account.name,
                displayName = accountManager.getUserData(account, KEY_DISPLAY_NAME),
                userId = accountManager.getUserData(account, KEY_USER_ID).toInt()
            )
        }
    }

    suspend fun removeAccount(login: String): Boolean {
        val account = getAccount(login) ?: return false

        val result = suspendCoroutine { continuation ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccount(account, null, { future ->
                    try {
                        continuation.resume(future.result
                            .getBoolean(AccountManager.KEY_BOOLEAN_RESULT))
                    } catch (e: Exception) {
                        continuation.resume(false)
                    }
                }, null)
            } else {
                @Suppress("DEPRECATION")
                accountManager.removeAccount(account, { future ->
                    try {
                        continuation.resume(future.result)
                    } catch (e: Exception) {
                        continuation.resume(false)
                    }
                }, null)
            }
        }
        refreshAccountsFlow()
        return result
    }

    private suspend fun createAccount(account: RcfhAccount): Boolean = withContext(Dispatchers.IO) {
        val acc = Account(account.login, ACCOUNT_TYPE)
        try {
            accountManager.addAccountExplicitly(acc, null, null).also { success ->
                if (success) {
                    accountManager.setUserData(acc, KEY_USER_ID, account.userId.toString())
                    accountManager.setUserData(acc, KEY_DISPLAY_NAME, account.displayName)
                }
            }
        } catch (e: SecurityException) {
            false
        }
    }

    private suspend fun updateAccount(account: RcfhAccount): Boolean =
        withContext(Dispatchers.IO) {
            val acc = getAccount(account.login) ?: return@withContext false
            try {
                accountManager.setUserData(acc, KEY_USER_ID, account.userId.toString())
                accountManager.setUserData(acc, KEY_DISPLAY_NAME, account.displayName)
                true
            } catch (e: SecurityException) {
                false
            }
        }

    private suspend fun getAllAccounts(): Array<Account> = withContext(Dispatchers.IO) {
        accountManager.getAccountsByType(ACCOUNT_TYPE)
    }

    private suspend fun getAccount(login: String): Account? = withContext(Dispatchers.IO) {
        accountManager.getAccountsByType(ACCOUNT_TYPE)
            .firstOrNull { it.name == login }
    }

    private suspend fun refreshAccountsFlow() {
        _accounts.value = getAccounts()
    }

    companion object {
        private const val ACCOUNT_TYPE = "ru.rcfh"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_DISPLAY_NAME = "display_name"
    }
}