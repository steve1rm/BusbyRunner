@file:OptIn(ExperimentalCoroutinesApi::class)

package me.androidbox.run.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class RunningTracker(
    private val locationObserver: LocationObserver,
    applicationScope: CoroutineScope
) {

    private val isObservingLocation = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentLocation = isObservingLocation
        .flatMapLatest { isObserving ->
            if(!isObserving) {
                locationObserver.observeLocation(1_000L)
            }
            else {
                emptyFlow()
            }
        }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            false)

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }
}