package me.androidbox.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    private val email: String,
    private val password: String
)
