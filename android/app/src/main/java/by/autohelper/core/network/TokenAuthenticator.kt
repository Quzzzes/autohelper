package by.autohelper.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.toString().contains("/auth/")) return null
        if (responseCount(response) >= 2) return null

        val refreshToken = tokenStorage.getRefreshToken() ?: return null
        val newTokens = runBlocking { refreshTokens(refreshToken) } ?: return null
        runBlocking { tokenStorage.saveTokens(newTokens.first, newTokens.second) }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.first}")
            .build()
    }

    private fun refreshTokens(refreshToken: String): Pair<String, String>? {
        return try {
            val body = """{"refreshToken":"$refreshToken"}"""
                .toRequestBody("application/json".toMediaType())

            val req = Request.Builder()
                .url("http://91.149.179.69/api/auth/refresh")
                .post(body)
                .build()

            val resp = OkHttpClient().newCall(req).execute()
            if (!resp.isSuccessful) return null

            val json = JSONObject(resp.body?.string() ?: return null)
            val data = json.optJSONObject("data") ?: return null
            val access  = data.optString("accessToken").takeIf  { it.isNotEmpty() } ?: return null
            val refresh = data.optString("refreshToken").takeIf { it.isNotEmpty() } ?: return null
            Pair(access, refresh)
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var r: Response? = response.priorResponse
        while (r != null) { count++; r = r.priorResponse }
        return count
    }
}
