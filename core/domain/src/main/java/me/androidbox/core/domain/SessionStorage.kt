package me.androidbox.core.domain

interface SessionStorage  {
    suspend fun get(): AuthorizationInfo?
    suspend fun set(authorizationInfo: AuthorizationInfo?)
}