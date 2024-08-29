package me.androidbox.wear.run.presentation.ambient

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.wear.ambient.AmbientLifecycleObserver

@Composable
fun AmbientObserver(
    onEnterAmbient: (AmbientLifecycleObserver.AmbientDetails) -> Unit,
    onExitAmbient: () -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(key1 = lifecycle) {
        val callback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
            override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                super.onEnterAmbient(ambientDetails)
                onEnterAmbient(ambientDetails)
            }

            override fun onExitAmbient() {
                super.onExitAmbient()
                onExitAmbient()
            }
        }

        /* Bound out the lifeycle so the ambient is only listening when the app is in the foreground */
        val observer = AmbientLifecycleObserver(context as ComponentActivity, callback)
        lifecycle.addObserver(observer)


        /* Called when disposableEffect leaves the composition as its no longer in the hierarchy */
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}