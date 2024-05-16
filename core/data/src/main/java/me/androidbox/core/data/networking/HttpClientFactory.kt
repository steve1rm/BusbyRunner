package me.androidbox.core.data.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.androidbox.core.database.BuildConfig
import me.androidbox.core.domain.AuthorizationInfo
import me.androidbox.core.domain.SessionStorage
import me.androidbox.core.domain.util.Result
import timber.log.Timber

class HttpClientFactory(
    private val sessionStorage: SessionStorage
) {
    fun build(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        this.ignoreUnknownKeys = true
                    }
                )
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.ALL
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                header("x-api-key", BuildConfig.API_KEY)
            }

            install(Auth) {
                this.bearer {
                    this.loadTokens {
                       sessionStorage.get()?.let { authorizationInfo ->
                            BearerTokens(
                                accessToken = authorizationInfo.accessToken,
                                refreshToken = authorizationInfo.refreshToken
                            )
                        }
                    }

                    /** ktor will take care of the 401 unauthorized request */
                    this.refreshTokens {
                        val authorizationInfo = sessionStorage.get()

                        authorizationInfo?.let { authInfo ->
                            val response = client.post<AccessTokenRequest, AccessTokenResponse>(
                                route = "/accessToken",
                                body = AccessTokenRequest(
                                    refreshToken = authInfo.refreshToken,
                                    userId = authInfo.userId
                                )
                            )

                            if (response is Result.Success) {
                                val newAuthorizationInfo = AuthorizationInfo(
                                    accessToken = response.data.accessToken,
                                    refreshToken = authInfo.refreshToken,
                                    userId = authInfo.userId
                                )

                                sessionStorage.set(newAuthorizationInfo)

                                BearerTokens(
                                    accessToken = newAuthorizationInfo.accessToken,
                                    refreshToken = newAuthorizationInfo.refreshToken
                                )
                            }
                            else {
                                BearerTokens(
                                    accessToken = "",
                                    refreshToken = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}