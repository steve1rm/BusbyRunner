package me.androidbox.auth.presentation.login

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.androidbox.auth.presentation.R
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.CheckIcon
import me.androidbox.core.presentation.designsystem.EmailIcon
import me.androidbox.core.presentation.designsystem.Poppins
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerActionButton
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerPasswordTextField
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerTextField
import me.androidbox.core.presentation.designsystem.components.GradientBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    loginState: LoginState,
    modifier: Modifier = Modifier,
    onLoginAction: (LoginAction) -> Unit
) {
    GradientBackground {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login_welcome),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = stringResource(id = R.string.welcome_message),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            BusbyRunnerTextField(
                state = loginState.email,
                startIcon = EmailIcon,
                endIcon = CheckIcon,
                hint = stringResource(id = R.string.example_email),
                title = stringResource(id = R.string.email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            BusbyRunnerPasswordTextField(
                state = loginState.password,
                hint = stringResource(id = R.string.password),
                title = stringResource(id = R.string.password),
                modifier = Modifier.fillMaxWidth(),
                isPasswordVisible = loginState.isPasswordVisible,
                onTogglePasswordVisibility = {
                    onLoginAction(LoginAction.OnTogglePasswordVisibility)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            BusbyRunnerActionButton(
                text = stringResource(id = R.string.login),
                isLoading = loginState.isLoggingIn,
                onClicked = {
                    onLoginAction(LoginAction.OnLoginClicked)
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val annotatedString = buildDescriptionAnnotatedString()

            ClickableText(text = annotatedString) { offSet ->
                annotatedString.getStringAnnotations(
                    tag = "clickable_text",
                    start = offSet,
                    end = offSet
                ).firstOrNull()?.let {
                    onLoginAction(LoginAction.OnRegisterClicked)
                }
            }
        }
    }
}

@Composable
private fun buildDescriptionAnnotatedString(): AnnotatedString {
    val annotatedString = buildAnnotatedString {
        this.withStyle(
            SpanStyle(
                fontFamily = Poppins,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            this.append(stringResource(id = R.string.dont_have_account))
            this.append(" ")
            this.pushStringAnnotation(
                tag = "clickable_text",
                annotation = stringResource(id = R.string.sign_up),
            )
        }

        this.withStyle(
            SpanStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            this.append(text = stringResource(id = R.string.sign_up))
        }
    }
    return annotatedString
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
fun PreviewLoginScreen() {
    BusbyRunnerTheme {
        LoginScreen(
            loginState = LoginState(),
            onLoginAction = {}
        )
    }
}

