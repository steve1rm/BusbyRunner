package me.androidbox.auth.data

import io.ktor.client.HttpClient
import me.androidbox.auth.domain.AuthorizationRepository
import me.androidbox.core.data.networking.post
import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.EmptyResult

class AuthorizationRepositoryImp(
    private val httpClient: HttpClient
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
        return httpClient.post<LoginRequest, Unit>(
            route = "/login",
            body = LoginRequest(
                email,
                password
            )
        )
    }
}