package ru.rcfh.core.account.service

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder

class RcfhAuthService : Service() {

    private lateinit var authenticator: RcfhAuthenticator

    override fun onCreate() {
        authenticator = RcfhAuthenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return authenticator.iBinder
    }

    class RcfhAuthenticator(context: Context) : AbstractAccountAuthenticator(context) {

        override fun addAccount(
            response: AccountAuthenticatorResponse?,
            accountType: String?,
            authTokenType: String?,
            requiredFeatures: Array<out String>?,
            options: Bundle?
        ): Bundle = Bundle.EMPTY

        override fun getAuthToken(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            authTokenType: String?,
            options: Bundle?
        ): Bundle = Bundle.EMPTY

        override fun confirmCredentials(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            options: Bundle?
        ): Bundle = Bundle.EMPTY

        override fun editProperties(
            response: AccountAuthenticatorResponse?,
            accountType: String?
        ): Bundle {
            return Bundle.EMPTY
        }

        override fun getAuthTokenLabel(authTokenType: String?) = authTokenType ?: ""

        override fun hasFeatures(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            features: Array<out String>?
        ): Bundle = Bundle.EMPTY

        override fun updateCredentials(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            authTokenType: String?,
            options: Bundle?
        ): Bundle = Bundle.EMPTY
    }
}