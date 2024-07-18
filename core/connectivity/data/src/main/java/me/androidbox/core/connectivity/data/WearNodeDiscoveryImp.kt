package me.androidbox.core.connectivity.data

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import me.androidbox.core.connectivity.domain.DeviceNode
import me.androidbox.core.connectivity.domain.DeviceType
import me.androidbox.core.connectivity.domain.NodeDiscovery
import java.util.UUID

class WearNodeDiscoveryImp(
    private val capabilityClient: CapabilityClient
) : NodeDiscovery {

    override fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>> {
        return callbackFlow<Set<DeviceNode>> {

            val remoteCapability = when (localDeviceType) {
                DeviceType.WATCH -> {
                    "busbyrunner_wear_app"
                }

                DeviceType.PHONE -> {
                    "busbyRunner_phone_app"
                }
            }

            try {
                val capability = capabilityClient.getCapability(
                    remoteCapability,
                    CapabilityClient.FILTER_REACHABLE
                ).await()

                val connectedDevices = capability.nodes.map { node ->
                    node.toDeviceNode()
                }.toSet()

                send(setOf(DeviceNode(
                    id = UUID.randomUUID().toString(),
                    displayName = "Test Watch",
                    isNearby = true
                )))

            } catch (apiException: ApiException) {
                if (BuildConfig.DEBUG) {
                    apiException.printStackTrace()
                }
                awaitClose()
                return@callbackFlow
            } catch (exception: Exception) {
                if (BuildConfig.DEBUG) {
                    exception.printStackTrace()
                }
                awaitClose()
                return@callbackFlow
            }

            val listener: (CapabilityInfo) -> Unit = {  capabilityInfo ->
                trySend(capabilityInfo.nodes.map { node ->
                    node.toDeviceNode()
                }.toSet())
            }

            capabilityClient.addListener(listener, remoteCapability)

            awaitClose {
                capabilityClient.removeListener(listener) }
        }
    }
}
