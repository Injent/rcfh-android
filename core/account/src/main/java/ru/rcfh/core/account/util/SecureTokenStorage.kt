package ru.rcfh.core.account.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class SecureTokenStorage(context: Context) {
    private val prefs by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createEncryptedPreferences(context)
        } else {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private val keyStore by lazy {
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            generateKeyStoreKeyIfNotExists()
        } else {
            generateLegacyKeyIfNotExists()
        }
    }

    // Асинхронное сохранение refresh токена по userId
    suspend fun saveRefreshToken(userId: Int, token: String) = withContext(Dispatchers.IO) {
        with(prefs.edit()) {
            val tokenKey = getTokenKey(userId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                putString(tokenKey, token)
            } else {
                putString(tokenKey, encryptToken(token))
            }
            commit()
        }
    }

    // Асинхронное получение refresh токена по userId
    suspend fun getRefreshToken(userId: Int): String? = withContext(Dispatchers.IO) {
        val tokenKey = getTokenKey(userId)
        val storedToken = prefs.getString(tokenKey, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            storedToken
        } else {
            storedToken?.let { decryptToken(it) }
        }
    }

    // Асинхронное удаление refresh токена по userId
    suspend fun clearRefreshToken(userId: Int) = withContext(Dispatchers.IO) {
        with(prefs.edit()) {
            val tokenKey = getTokenKey(userId)
            remove(tokenKey)
            commit()
        }
    }

    // Асинхронная проверка наличия токена по userId
    suspend fun hasRefreshToken(userId: Int): Boolean = withContext(Dispatchers.IO) {
        val tokenKey = getTokenKey(userId)
        prefs.contains(tokenKey)
    }

    // Генерация ключа для API 23+ с использованием KeyStore
    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateKeyStoreKeyIfNotExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    // Генерация и сохранение ключа для API 21-22
    private fun generateLegacyKeyIfNotExists() {
        if (!prefs.contains(KEY_ENCRYPTION_KEY)) {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256, SecureRandom())
            val secretKey = keyGenerator.generateKey()
            val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
            with(prefs.edit()) {
                putString(KEY_ENCRYPTION_KEY, encodedKey)
                apply()
            }
        }
    }

    // Получение ключа для API 21-22
    private fun getLegacyKey(): SecretKey {
        val encodedKey = prefs.getString(KEY_ENCRYPTION_KEY, null)
            ?: throw IllegalStateException("Encryption key not found")
        val keyBytes = Base64.decode(encodedKey, Base64.DEFAULT)
        return SecretKeySpec(keyBytes, "AES")
    }

    // Шифрование токена для API 21-22
    private fun encryptToken(token: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getLegacyKey())
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        val combined = iv + encryptedBytes
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    // Расшифровка токена для API 21-22
    private fun decryptToken(encryptedToken: String): String {
        val decodedBytes = Base64.decode(encryptedToken, Base64.DEFAULT)
        val iv = decodedBytes.copyOfRange(0, 12) // GCM IV обычно 12 байт
        val encryptedData = decodedBytes.copyOfRange(12, decodedBytes.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getLegacyKey(), javax.crypto.spec.GCMParameterSpec(128, iv))
        val decryptedBytes = cipher.doFinal(encryptedData)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun createEncryptedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun getTokenKey(userId: Int) = "refresh_token_$userId"

    companion object {
        private const val PREFS_NAME = "secure_token_prefs"
        private const val KEY_ENCRYPTION_KEY = "encryption_key"
        private const val KEY_ALIAS = "token_encryption_key"
    }
}