package me.androidbox.core.data.auth

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.androidbox.core.domain.AuthorizationInfo
import me.androidbox.core.domain.SessionStorage

class EncryptedSessionStorageImp(
    private val sharedPreferences: SharedPreferences
) : SessionStorage {

    companion object {
        private const val KEY_AUTH_INFO = "KEY_AUTH_INFO"
    }

    override suspend fun get(): AuthorizationInfo? {
        return withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(KEY_AUTH_INFO, null)

            json?.let {
                Json.decodeFromString<AuthorizationSerializable>(json).toAuthorizationInfo()

            }
        }
    }

    override suspend fun set(authorizationInfo: AuthorizationInfo?) {
        withContext(Dispatchers.IO) {
            if(authorizationInfo == null) {
                sharedPreferences
                    .edit()
                    .remove(KEY_AUTH_INFO)
                    .apply()
            }
            else {
                val json = Json.encodeToString(authorizationInfo.toAuthorizationSerializable())

                sharedPreferences
                    .edit()
                    .putString(KEY_AUTH_INFO, json)
                    .commit()
            }
        }
    }
}