@file:OptIn(ExperimentalCoroutinesApi::class)

package me.androidbox.wear.run.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import me.androidbox.core.connectivity.domain.DeviceNode
import me.androidbox.core.connectivity.domain.DeviceType
import me.androidbox.core.connectivity.domain.NodeDiscovery
import me.androidbox.core.connectivity.domain.messaging.MessagingAction
import me.androidbox.core.connectivity.domain.messaging.MessagingClient
import me.androidbox.core.connectivity.domain.messaging.MessagingError
import me.androidbox.core.domain.util.EmptyResult
import me.androidbox.wear.run.domain.PhoneConnector

class WatchToPhoneConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient
) : PhoneConnector {

    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedNode: StateFlow<DeviceNode?>
        get() {
            return _connectedNode.asStateFlow()
        }

    override val messagingActions = nodeDiscovery
        .observeConnectedDevices(DeviceType.WATCH) // Phone Connected, this from the perspective of the watch
        .flatMapLatest { connectedNode ->
            val node = connectedNode.firstOrNull()

            if(node != null && node.isNearby) {
                _connectedNode.value = node
                messagingClient.connectToNode(node.id)
            }
            else {
                emptyFlow()
            }
        }
        .shareIn(applicationScope, SharingStarted.Eagerly)


    override suspend fun sendActionToPhone(messagingAction: MessagingAction): EmptyResult<MessagingError> {
        return messagingClient.sendOrQueryAction(messagingAction)
    }
}