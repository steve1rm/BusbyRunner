package me.androidbox.wear.run.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.androidbox.core.connectivity.domain.DeviceNode
import me.androidbox.core.connectivity.domain.messaging.MessagingAction
import me.androidbox.core.connectivity.domain.messaging.MessagingError
import me.androidbox.core.domain.util.EmptyResult

interface PhoneConnector {
    val connectedNode: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToPhone(messagingAction: MessagingAction): EmptyResult<MessagingError>
}