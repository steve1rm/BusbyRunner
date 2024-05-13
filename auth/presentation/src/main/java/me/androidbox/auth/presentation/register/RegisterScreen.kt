package me.androidbox.auth.presentation.register

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.androidbox.auth.presentation.R
import me.androidbox.core.presentation.designsystem.BusbyGray
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.components.GradientBackground
import me.androidbox.core.presentation.designsystem.Poppins

@Composable
fun RegisterScreen(
    registerState: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
    GradientBackground(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.create_account),
                    style = MaterialTheme.typography.headlineMedium)

                val annotatedString = buildAnnotatedString {
                    this.withStyle(
                        SpanStyle(
                            fontFamily = Poppins,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        this.append(stringResource(id = R.string.existing_account))
                        this.append(" ")
                        this.pushStringAnnotation(
                            tag = "clickable_text",
                            annotation = stringResource(id = R.string.login),
                        )
                    }

                    this.withStyle(
                        SpanStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        this.append(text = stringResource(id = R.string.login))
                    }
                }

                ClickableText(text = annotatedString) { offSet ->
                    annotatedString.getStringAnnotations(
                        tag = "clickable_text",
                        start = offSet,
                        end = offSet
                    ).firstOrNull()?.let {
                        onAction(RegisterAction.OnLoginClicked)
                    }
                }
            }
        }
    )
}


@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewRegisterScreen() {
    BusbyRunnerTheme {
        RegisterScreen(
            registerState = RegisterState(),
            onAction = {} )
    }
}