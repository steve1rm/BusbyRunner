package me.androidbox.wear.run.domain

import kotlinx.coroutines.flow.StateFlow
import me.androidbox.core.connectivity.domain.DeviceNode

interface PhoneConnector {
    val connectedNode: StateFlow<DeviceNode?>

    fun setIsTrackable(isTrackable: Boolean)

}