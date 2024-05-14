package me.androidbox.auth.domain

import me.androidbox.core.domain.util.DataError
import me.androidbox.core.domain.util.EmptyResult

interface AuthorizationRepository {
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
}