package me.androidbox.auth.data

import io.ktor.client.HttpClient
import me.androidbox.auth.domain.AuthorizationRepository
import me.androidbox.core.data.networking.post
import me.androidbox.core.domain.AuthorizationInfo
import me.androidbox.core.domain.SessionStorage
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.EmptyResult
import me.androidbox.core.domain.util.Result
import me.androidbox.core.domain.util.asEmptyResult

class AuthorizationRepositoryImp(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
) : AuthorizationRepository {
    override suspend fun register(
        email: String,
        password: String
    ): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password)
        )
    }

    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = "/login",
            body = LoginRequest(
                email,
                password
            )
        )

        if(result is Result.Success) {
            sessionStorage.set(
                AuthorizationInfo(
                accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId)
            )
        }

        return result.asEmptyResult()
    }
}