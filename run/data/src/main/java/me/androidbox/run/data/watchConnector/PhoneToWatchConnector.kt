@file:Suppress("OPT_IN_USAGE")

package me.androidbox.run.data.watchConnector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import me.androidbox.core.connectivity.domain.DeviceNode
import me.androidbox.core.connectivity.domain.DeviceType
import me.androidbox.core.connectivity.domain.NodeDiscovery
import me.androidbox.core.connectivity.domain.messaging.MessagingAction
import me.androidbox.core.connectivity.domain.messaging.MessagingClient
import me.androidbox.core.connectivity.domain.messaging.MessagingError
import me.androidbox.core.domain.util.EmptyResult
import me.androidbox.run.domain.WatchConnector

class PhoneToWatchConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient
) : WatchConnector {

    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevices: StateFlow<DeviceNode?>
        get() {
            return _connectedNode.asStateFlow()
        }

    private val isTrackable = MutableStateFlow(false)

    init {
        _connectedNode
            .filterNotNull() // Until we have a connected node
            .flatMapLatest {
                isTrackable
            }
            .onEach { isTrackable ->
                sendActionToWatch(messagingAction = MessagingAction.ConnectionRequest)

                val messagingAction = if(isTrackable) {
                    MessagingAction.Trackable
                } else {
                    MessagingAction.UnTrackable
                }
                sendActionToWatch(messagingAction = messagingAction)
            }
            .launchIn(applicationScope)
    }

    override val messagingActions = nodeDiscovery.observeConnectedDevices(DeviceType.PHONE)
        .flatMapLatest { setOfConnectedNodes ->
            val node = setOfConnectedNodes.firstOrNull()

            if(node != null && node.isNearby) {
                _connectedNode.value = node
                messagingClient.connectToNode(node.id)
            }
            else {
                emptyFlow()
            }.onEach { messagingAction ->
                    if(messagingAction == MessagingAction.ConnectionRequest) {
                        if(isTrackable.value) {
                            sendActionToWatch(MessagingAction.Trackable)
                        }
                        else {
                            sendActionToWatch(MessagingAction.UnTrackable)
                        }
                    }
                    else {
                        setIsTrackable(false)
                    }
                }
        }.shareIn(applicationScope, SharingStarted.Eagerly)

    override fun setIsTrackable(isTrackable: Boolean) {
        this.isTrackable.value = isTrackable
    }

    override suspend fun sendActionToWatch(messagingAction: MessagingAction): EmptyResult<MessagingError> {
        return messagingClient.sendOrQueryAction(messagingAction)
    }
}