package me.androidbox.core.presentation.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme

@Composable
fun BusbyRunnerScaffold(
    modifier: Modifier = Modifier,
    content: @Composable (paddingValues: PaddingValues) -> Unit,
    withGradient: Boolean = true,
    topAppBar: @Composable () -> Unit = {},
    floatActionButton: @Composable () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = topAppBar,
        floatingActionButton = floatActionButton,
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        if (withGradient) {
            GradientBackground {
                content(paddingValues = paddingValues)
            }
        }
        else {
            content(paddingValues = paddingValues)
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
fun PreviewBusbyRunnerScaffold() {
    BusbyRunnerTheme {
        BusbyRunnerScaffold(
            withGradient = false,
            content = {}
        )
    }
}