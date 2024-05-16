package me.androidbox.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    private val email: String,
    private val password: String
)
