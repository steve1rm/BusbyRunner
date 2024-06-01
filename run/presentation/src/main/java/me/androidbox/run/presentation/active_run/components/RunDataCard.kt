package me.androidbox.run.presentation.active_run.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.ui.formatted
import me.androidbox.core.presentation.ui.toFormattedKm
import me.androidbox.core.presentation.ui.toFormattedPace
import me.androidbox.run.domain.RunData
import me.androidbox.run.presentation.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun RunDataCard(
    elapsedTime: Duration,
    runData: RunData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        RunDataItem(
            title = stringResource(R.string.duration),
            value = elapsedTime.formatted(),
            valueFontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RunDataItem(
                modifier = Modifier.widthIn(min = 80.dp),
                title = stringResource(R.string.distance),
                value = (runData.distanceMeters / 1000.0).toFormattedKm(),
            )

            RunDataItem(
                modifier = Modifier.widthIn(min = 80.dp),
                title = stringResource(R.string.heart_rate),
                value = 49.toString(),
            )

            RunDataItem(
                modifier = Modifier.widthIn(min = 80.dp),
                title = stringResource(R.string.pace),
                value = elapsedTime.toFormattedPace(distanceKm = (runData.distanceMeters / 1000.0)),
            )
        }
    }
}

@Composable
private fun RunDataItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = 16.sp
) {
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )

        Text(
            text = value,
            fontSize = valueFontSize,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewRunDataItem() {
   BusbyRunnerTheme {
       RunDataItem(
           title = "Pace",
           value = 12.minutes.toString()
       )
   }
}

@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewRunDataCard() {
    BusbyRunnerTheme {
        RunDataCard(
            elapsedTime = 60.minutes,
            runData = RunData(
                distanceMeters = 6783,
                pace = 3.minutes
            )
        )
    }
}

