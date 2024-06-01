@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package me.androidbox.run.presentation.run_overview.components

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.androidbox.core.domain.location.Latitude
import me.androidbox.core.domain.location.Location
import me.androidbox.core.domain.location.Longitude
import me.androidbox.core.domain.run.RunModel
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.CalendarIcon
import me.androidbox.core.presentation.designsystem.RunIcon
import me.androidbox.core.presentation.designsystem.RunOutlinedIcon
import me.androidbox.core.presentation.designsystem.components.util.DropDownItem
import me.androidbox.run.presentation.R
import me.androidbox.run.presentation.run_overview.mappers.toRunUi
import me.androidbox.run.presentation.run_overview.model.RunDataUi
import me.androidbox.run.presentation.run_overview.model.RunUi
import java.time.ZonedDateTime
import kotlin.math.max
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunListItem(
    runUi: RunUi,
    modifier: Modifier = Modifier,
    onDeleteClicked: () -> Unit
) {
    var shouldShowDropdown by remember {
        mutableStateOf(false)
    }

    Box {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surface)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        shouldShowDropdown = true
                    }
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MapImage(imageUrl = runUi.mapPictureUrl)
            RunningTimeSection(
                duration = runUi.duration,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            RunningDateSection(dateTime = runUi.dateTime, modifier = Modifier)
            DataGrid(runUi = runUi)
        }

        DropdownMenu(
            expanded = shouldShowDropdown,
            onDismissRequest = { shouldShowDropdown = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.delete_run))
                },
                onClick = {
                    shouldShowDropdown = false
                    onDeleteClicked()
                }
            )
        }
    }
}

@Composable
private fun RunningDateSection(dateTime: String, modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = dateTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RunningTimeSection(
    duration: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
              imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.total_running_time),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DataGrid(
    runUi: RunUi,
    modifier: Modifier = Modifier
) {
    val runDataUiList = listOf(
        RunDataUi(
            name = stringResource(id = R.string.distance),
            value = runUi.distance
        ),
        RunDataUi(
            name = stringResource(id = R.string.pace),
            value = runUi.pace
        ),
        RunDataUi(
            name = stringResource(id = R.string.avg_speed),
            value = runUi.avgSpeed
        ),
        RunDataUi(
            name = stringResource(id = R.string.max_speed),
            value = runUi.maxSpeed
        ),
        RunDataUi(
            name = stringResource(id = R.string.total_elevation),
            value = runUi.totalElevation
        ),
    )

    var maxWidth by remember {
        mutableIntStateOf(0)
    }
    val maxWidthDp = with(LocalDensity.current) {
       maxWidth.toDp()
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        runDataUiList.forEach { runDataUi ->
            DataGridCell(
                runDataUi = runDataUi,
                modifier = Modifier
                    .defaultMinSize(minWidth = maxWidthDp)
                    .onSizeChanged { size ->
                        maxWidth = max(maxWidth, size.width)
                    })
        }
    }
}

@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewRunListItem() {
    BusbyRunnerTheme {
        RunListItem(
            runUi = RunModel(
                id = "123",
                duration = 10.minutes + 30.seconds,
                dateTimeUtc = ZonedDateTime.now(),
                distanceMeters = 2543,
                location = Location(Latitude(0.0), Longitude(0.0)),
                maxSpeedKmh = 15.6234,
                totalElevationMeters = 123,
                mapPictureUrl = null
            ).toRunUi(),
            onDeleteClicked = {}
        )
    }
}