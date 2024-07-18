package me.androidbox.run.data.watchConnector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.androidbox.core.connectivity.domain.DeviceNode
import me.androidbox.core.connectivity.domain.DeviceType
import me.androidbox.core.connectivity.domain.NodeDiscovery
import me.androidbox.run.domain.WatchConnector

class PhoneToWatchConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope
) : WatchConnector {

    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevices: StateFlow<DeviceNode?>
        get() {
            return _connectedNode.asStateFlow()
        }

    private val isTrackable = MutableStateFlow(false)

    val messagingActions = nodeDiscovery.observeConnectedDevices(DeviceType.PHONE)
        .onEach { setOfConnectedNodes ->
            val node = setOfConnectedNodes.firstOrNull()

            if(node != null && node.isNearby) {
                _connectedNode.value = node
            }
        }.launchIn(applicationScope)

    override fun setIsTrackable(isTrackable: Boolean) {
        this.isTrackable.value = isTrackable
    }
}