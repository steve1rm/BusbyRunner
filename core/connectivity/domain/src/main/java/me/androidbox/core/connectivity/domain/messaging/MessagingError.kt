package me.androidbox.core.connectivity.domain.messaging

import me.androidbox.core.domain.util.Error

enum class MessagingError : Error {
    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN
}