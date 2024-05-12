package me.androidbox.core.presentation.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.R

@Composable
fun BusbyRunnerActionButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClicked: () -> Unit
) {
    Button(
        modifier = modifier
            .height(IntrinsicSize.Min), /** Sets the height to the minimum height of one of its children */
        enabled = isEnabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        border = BorderStroke(width = 0.6.dp, color = MaterialTheme.colorScheme.onBackground),
        shape = CircleShape.copy(CornerSize(100f)),
        onClick = {
            onClicked()
        }) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center) {

            /** Using alpha to hide or show, which is the same as invisible or visible,
             *  so will retain the height of the button */
            CircularProgressIndicator(
                modifier = Modifier
                    .size(16.dp)
                    .alpha(if (isLoading) 1f else 0f),
                strokeWidth = 1.6.dp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                modifier = Modifier.alpha(if(isLoading) 0f else 1f),
                text = text,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewBusbyRunnerOutlineActionButton() {
    BusbyRunnerTheme {
        BusbyRunnerOutlineActionButton(
            text = stringResource(id = R.string.sign_up),
            isLoading = false) {
        }
    }
}
