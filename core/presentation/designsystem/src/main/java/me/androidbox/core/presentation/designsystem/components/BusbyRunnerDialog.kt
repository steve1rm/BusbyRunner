package me.androidbox.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme

@Composable
fun BusbyRunnerDialog(
    modifier: Modifier = Modifier,
    title: String,
    onDismiss: () -> Unit,
    description: String,
    primaryButton: @Composable RowScope.() -> Unit,
    secondaryButton: @Composable (RowScope.() -> Unit)? = null,
) {
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Column(modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface)

            Text(text = description,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                secondaryButton?.invoke(this)
                primaryButton()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewBusbyRunnerDialog() {
    BusbyRunnerTheme {
        BusbyRunnerDialog(
            title = "notification",
            onDismiss = {},
            description = "Resume or finish your run",
            primaryButton = {
                BusbyRunnerOutlineActionButton(
                    text = "Resume", isLoading = false ) {
                }
            },
            secondaryButton ={
                BusbyRunnerOutlineActionButton(
                    text = "Finish", isLoading = false ) {
                }
            },
            modifier = Modifier,
        )
    }
}