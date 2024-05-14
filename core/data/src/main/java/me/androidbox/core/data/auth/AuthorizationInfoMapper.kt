package me.androidbox.core.data.auth

import me.androidbox.core.domain.AuthorizationInfo

fun AuthorizationInfo.toAuthorizationSerializable(): AuthorizationSerializable {
    return AuthorizationSerializable(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        userId = this.userId
    )
}

fun AuthorizationInfo.toAuthorizationInfo(): AuthorizationInfo {
    return AuthorizationInfo(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        userId = this.userId
    )
}


