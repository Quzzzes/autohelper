package by.autohelper.core.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_tokens")

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val ACCESS_TOKEN  = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

    // Для OkHttp interceptor (вызов вне корутины) — работает на IO потоке
    fun getAccessToken(): String? = runBlocking(Dispatchers.IO) {
        context.dataStore.data.first()[ACCESS_TOKEN]
    }

    fun getRefreshToken(): String? = runBlocking(Dispatchers.IO) {
        context.dataStore.data.first()[REFRESH_TOKEN]
    }

    // Flow для подписки в ViewModel
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[ACCESS_TOKEN] != null }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { prefs ->
                prefs[ACCESS_TOKEN]  = accessToken
                prefs[REFRESH_TOKEN] = refreshToken
            }
        }
    }

    suspend fun clearTokens() {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { it.clear() }
        }
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null
}
