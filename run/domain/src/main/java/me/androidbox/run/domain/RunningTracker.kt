@file:OptIn(ExperimentalCoroutinesApi::class)

package me.androidbox.run.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import me.androidbox.core.domain.Timer
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    private val _runDataState = MutableStateFlow(RunData())
    val runDataState = _runDataState.asStateFlow()

    private val _isTrackingState = MutableStateFlow(false)
    val isTrackingState = _isTrackingState.asStateFlow()

    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTimeState = MutableStateFlow(Duration.ZERO)
    val elapsedTimeState = _elapsedTimeState.asStateFlow()

    fun setIsTracking(isTracking: Boolean) {
        _isTrackingState.value = isTracking
    }

    val currentLocation = isObservingLocation
        .flatMapLatest { isObserving ->
            if(!isObserving) {
                locationObserver.observeLocation(1_000L)
            }
            else {
                flowOf()
            }
        }
        .stateIn(
            applicationScope,
            SharingStarted.Eagerly,
            null)

    init {
        _isTrackingState
            .onEach { isTracking ->
                if(!isTracking) {
                    val newList = buildList {
                        addAll(runDataState.value.locations)
                        add(emptyList<LocationTimestamp>())
                    }.toList()

                    _runDataState.update { runData ->
                        runData.copy(
                            locations = newList
                        )
                    }
                }
            }
            .flatMapLatest { isTracking ->
                if(isTracking) {
                    Timer.timeAndEmit()
                }
                else {
                    emptyFlow()
                }
            }
            .onEach { duration ->
                _elapsedTimeState.value += duration
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(_isTrackingState) { currentLocation, isTracking ->
                if(isTracking) {
                    emit(currentLocation)
                }
            }
            .zip(_elapsedTimeState) { location, elapsedDuration ->
                LocationTimestamp(
                    location = location,
                    durationTimestamps = elapsedDuration
                )
            }
            .onEach { locationTimestamp ->
                val currentLocations = runDataState.value.locations

                val lastLocationList = if(currentLocations.isNotEmpty()) {
                    currentLocations.last() + locationTimestamp
                }
                else {
                    listOf(locationTimestamp)
                }

                val newLocationList = currentLocations.replaceList(lastLocationList)

                val distanceMeters = LocationDataCalculator.getTotalDistanceMeters(
                    locations = newLocationList
                )
                val distanceKm = distanceMeters / 1_000.0
                val currentDuration = locationTimestamp.durationTimestamps
                val avgSecondsPerKm = if(distanceKm == 0.0) {
                    0
                }
                else {
                    (currentDuration.inWholeSeconds / distanceKm).roundToInt()
                }

                _runDataState.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocationList
                    )
                }
            }
            .launchIn(applicationScope)
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }
}

private fun <T> List<List<T>>.replaceList(replacement: List<T>): List<List<T>> {
    return if(this.isEmpty()) {
        listOf(replacement)
    }
    else {
        this.dropLast(1) + listOf(replacement)
    }
}