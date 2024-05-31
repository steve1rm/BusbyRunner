package me.androidbox.run.presentation.active_run.service

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.androidbox.core.presentation.ui.formatted
import me.androidbox.run.domain.RunningTracker
import me.androidbox.run.presentation.R
import org.koin.android.ext.android.inject

class ActiveRunService : Service() {
    companion object {
        /** we have an active run even if paused or resumed */
        var isServiceActive: Boolean = false

        private const val CHANNEL_ID = "active_run"
        private val ACTION_START = "action_start"
        private val ACTION_STOP = "action_stop"
        private const val EXTRA_ACTIVITY_CLASS = "extra_activity_class"

        fun <T: Activity> createStartIntent(context: Context, activityClass: Class<out T>): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                this.action = ACTION_START
                this.putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                this.action = ACTION_STOP
            }
        }
    }

    private val runnerTracker by inject<RunningTracker>()
    private var activeRunServiceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val notificationManager by lazy {
        this.getSystemService<NotificationManager>()!!
    }

    private val baseNotification by lazy {
        NotificationCompat
            .Builder(this.applicationContext, CHANNEL_ID)
            .setOnlyAlertOnce(true)
            .setSmallIcon(me.androidbox.core.presentation.designsystem.R.drawable.logo)
            .setContentTitle(this.getString(R.string.active_run))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_CLASS)
                    ?: throw IllegalArgumentException("No activity class provided")

                start(Class.forName(activityClass))
            }
            ACTION_STOP -> {
                stop()
            }
        }
        return START_STICKY
    }

    private fun <T> start(activityClass: Class<T>) {
        if(!isServiceActive) {
            isServiceActive = true
            createNotificationChannel()

            val activityIntent = Intent(this.applicationContext, activityClass).apply {
                this.data = "busbyrunner://active_run".toUri()
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            val pendingIntent = TaskStackBuilder.create(this.applicationContext)
                .addNextIntentWithParentStack(activityIntent)
                .getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)

            val notification = baseNotification
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent)
                .build()

            this.startForeground(1, notification)
            updateNotification()
        }
    }

    private fun stop() {
        this.stopSelf()
        isServiceActive = false

        /** Once the scope has been cancelled it won't be able to be started again,
         * so have to initialize a new job with a dispatcher */
        // activeRunServiceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        /** Update only cancels the children so we don't have to re-initialize the scope again */
        activeRunServiceScope.coroutineContext.cancelChildren()
    }

    private fun updateNotification() {
        runnerTracker.elapsedTimeState.onEach { elapsedTime ->
            val notification = baseNotification
                .setContentText(elapsedTime.formatted())
                .build()

            notificationManager.notify(1, notification)
        }.launchIn(activeRunServiceScope)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                this.getString(R.string.active_run),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}






/*
inline fun <reified T : Any> getSystemName() {
    serviceClassName(T::class.java)
}

fun <T> serviceClassName(nameOfClass: Class<T>) {
    println(nameOfClass)
}
*/

