package me.androidbox.wear.run.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesException
import androidx.health.services.client.clearUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import me.androidbox.core.domain.util.EmptyResult
import me.androidbox.core.domain.util.Result
import me.androidbox.wear.run.domain.ExerciseError
import me.androidbox.wear.run.domain.ExerciseTracker
import kotlin.math.roundToInt

class HealthExerciseTrackerImp(
    private val context: Context,
    private val client: ExerciseClient
) : ExerciseTracker {

    override val heartRate: Flow<Int>
        get() = callbackFlow {
            val callback = object : ExerciseUpdateCallback {
                override fun onAvailabilityChanged(
                    dataType: DataType<*, *>,
                    availability: Availability
                ) {
                    return
                }

                override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                    val heartRate = update.latestMetrics.getData(DataType.HEART_RATE_BPM)
                    val currentHeartRate = heartRate.firstOrNull()?.value

                    currentHeartRate?.let { heartRate ->
                        trySend(heartRate.roundToInt())
                    }
                }

                override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
                    return
                }

                override fun onRegistered() {
                    return
                }

                override fun onRegistrationFailed(throwable: Throwable) {
                    if(BuildConfig.DEBUG) {
                        throwable.printStackTrace()
                    }
                }
            }
            client.setUpdateCallback(callback)

            awaitClose {
                runBlocking {
                    client.clearUpdateCallback(callback)
                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun isHeartRateTrackingSupported(): Boolean {
        return hasBodySensorsPermission() && runCatching {
            val capabilities = client.getCapabilities()
            val supportedDataTypes = capabilities.typeToCapabilities[ExerciseType.RUNNING]
                ?.supportedDataTypes ?: emptySet()

            // Alternative using in operator DataType.HEART_RATE_BPM in supportedDataTypes

            supportedDataTypes.contains(DataType.HEART_RATE_BPM)
        }.getOrDefault(false)
    }

    override suspend fun prepareExercise(): EmptyResult<ExerciseError> {
        val result = if(!isHeartRateTrackingSupported()) {
            Result.Failure(ExerciseError.TRACKING_NOT_SUPPORTED)
        }
        else {
            val result = getActiveExerciseInfo()

            if(result is Result.Failure) {
               result
            }
            else {
                val warmUpConfig = WarmUpConfig(
                    exerciseType = ExerciseType.RUNNING,
                    dataTypes = setOf(DataType.HEART_RATE_BPM)
                )

                client.prepareExercise(warmUpConfig)

                Result.Success(Unit)
            }
        }

        return result
    }

    override suspend fun startExercise(): EmptyResult<ExerciseError> {
        val result = if(!isHeartRateTrackingSupported()) {
            Result.Failure(ExerciseError.TRACKING_NOT_SUPPORTED)
        }
        else {
            val result = getActiveExerciseInfo()

            if(result is Result.Failure) {
                result
            }
            else {
                val exerciseConfig = ExerciseConfig
                    .builder(ExerciseType.RUNNING)
                    .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                    .setIsAutoPauseAndResumeEnabled(false)
                    .build()

                client.startExercise(exerciseConfig)

                Result.Success(Unit)
            }
        }

        return result
    }

    override suspend fun pauseExercise(): EmptyResult<ExerciseError> {
        val result = if(!isHeartRateTrackingSupported()) {
            Result.Failure(ExerciseError.TRACKING_NOT_SUPPORTED)
        }
        else {
            val result = getActiveExerciseInfo()

            if(result is Result.Failure && result.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
                result
            }
            else {
                try {
                    client.pauseExercise()
                    Result.Success(Unit)
                }
                catch (exception: HealthServicesException) {
                    Result.Failure(ExerciseError.EXERCISE_ALREADY_ENDED)
                }
                catch (exception: Exception) {
                    Result.Failure(ExerciseError.UNKNOWN)
                }

                Result.Success(Unit)
            }
        }

        return result
    }

    override suspend fun resumeExercise(): EmptyResult<ExerciseError> {
        val result = if(!isHeartRateTrackingSupported()) {
            Result.Failure(ExerciseError.TRACKING_NOT_SUPPORTED)
        }
        else {
            val result = getActiveExerciseInfo()

            if(result is Result.Failure && result.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
                result
            }
            else {
                try {
                    client.resumeExercise()
                    Result.Success(Unit)
                }
                catch (exception: HealthServicesException) {
                    Result.Failure(ExerciseError.EXERCISE_ALREADY_ENDED)
                }
                catch (exception: Exception) {
                    Result.Failure(ExerciseError.UNKNOWN)
                }

                Result.Success(Unit)
            }
        }

        return result
    }

    override suspend fun stopExercise(): EmptyResult<ExerciseError> {
        val result = if(!isHeartRateTrackingSupported()) {
            Result.Failure(ExerciseError.TRACKING_NOT_SUPPORTED)
        }
        else {
            val result = getActiveExerciseInfo()

            if(result is Result.Failure && result.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
                result
            }
            else {
                try {
                    client.endExercise()
                    Result.Success(Unit)
                }
                catch (exception: HealthServicesException) {
                    Result.Failure(ExerciseError.EXERCISE_ALREADY_ENDED)
                }
                catch (exception: Exception) {
                    Result.Failure(ExerciseError.UNKNOWN)
                }

                Result.Success(Unit)
            }
        }

        return result
    }

    private suspend fun getActiveExerciseInfo(): EmptyResult<ExerciseError> {
        val exerciseInfo = client.getCurrentExerciseInfo()

        return when(exerciseInfo.exerciseTrackedStatus) {
            ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS -> {
                Result.Success(Unit)
            }
            ExerciseTrackedStatus.OWNED_EXERCISE_IN_PROGRESS -> {
                Result.Failure(ExerciseError.ONGOING_OWN_EXERCISE)
            }
            ExerciseTrackedStatus.OTHER_APP_IN_PROGRESS -> {
                Result.Failure(ExerciseError.ONGOING_OTHER_EXERCISE)
            }
            else -> {
                Result.Failure(ExerciseError.UNKNOWN)
            }
        }
    }

    private fun hasBodySensorsPermission(): Boolean {
        return context.checkSelfPermission(Manifest.permission.BODY_SENSORS) ==
                PackageManager.PERMISSION_GRANTED
    }
}