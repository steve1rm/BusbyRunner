package me.androidbox.run.domain

import kotlinx.coroutines.flow.StateFlow
import me.androidbox.core.connectivity.domain.DeviceNode

interface WatchConnector {
    val connectedDevices: StateFlow<DeviceNode?>

    fun setIsTrackable(isTrackable: Boolean)
}