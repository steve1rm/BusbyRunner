package me.androidbox.core.data.auth

import kotlinx.serialization.Serializable

@Serializable
class AuthorizationSerializable(
    /** Short lived token */
    val accessToken: String,
    /** Long lived token to be used to request another accessToken */
    val refreshToken: String,
    val userId: String
)