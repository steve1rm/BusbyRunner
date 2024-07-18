package me.androidbox.core.connectivity.domain.messaging

import kotlinx.coroutines.flow.Flow
import me.androidbox.core.domain.util.EmptyResult

interface MessagingClient {
    fun connectToNode(nodeId: String): Flow<MessagingAction>
    suspend fun sendOrQueryAction(messagingAction: MessagingAction): EmptyResult<MessagingError>
}