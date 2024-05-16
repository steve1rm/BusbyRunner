package me.androidbox.auth.presentation.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.androidbox.auth.presentation.R
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.LogoIcon
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerActionButton
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerOutlineActionButton
import me.androidbox.core.presentation.designsystem.components.GradientBackground

@Composable
fun IntroScreen(
    onAction: (IntroAction) -> Unit
) {
    GradientBackground(
        content = {
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                BusbyRunnerLogoVertical()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 48.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_to_busbyrunner),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground)

                Text(
                    text = stringResource(id = R.string.busbyrunner_description),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(32.dp))

                BusbyRunnerActionButton(
                    text = stringResource(id = R.string.sign_in),
                    isLoading = false
                ) {
                    onAction(IntroAction.OnSignInClicked)
                }

                Spacer(modifier = Modifier.height(16.dp))

                BusbyRunnerOutlineActionButton(
                    text = stringResource(id = R.string.sign_up), isLoading = false
                ) {
                    onAction(IntroAction.OnSignUpClicked)
                }
            }
    })
}

@Composable
private fun BusbyRunnerLogoVertical(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Icon(
            imageVector = LogoIcon,
            contentDescription = "Intro screen logo",
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = R.string.busbyrunner),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewIntroScreen() {
    BusbyRunnerTheme {
        IntroScreen {
        }
    }
}