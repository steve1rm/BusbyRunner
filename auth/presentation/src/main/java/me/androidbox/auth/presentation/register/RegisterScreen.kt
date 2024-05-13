package me.androidbox.auth.presentation.register

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.androidbox.auth.domain.UserDataValidator
import me.androidbox.auth.presentation.R
import me.androidbox.auth.presentation.auth.IntroAction
import me.androidbox.core.presentation.designsystem.BusbyDarkRed
import me.androidbox.core.presentation.designsystem.BusbyGreen
import me.androidbox.core.presentation.designsystem.BusbyRunnerTheme
import me.androidbox.core.presentation.designsystem.CheckIcon
import me.androidbox.core.presentation.designsystem.CrossIcon
import me.androidbox.core.presentation.designsystem.EmailIcon
import me.androidbox.core.presentation.designsystem.components.GradientBackground
import me.androidbox.core.presentation.designsystem.Poppins
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerActionButton
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerPasswordTextField
import me.androidbox.core.presentation.designsystem.components.BusbyRunnerTextField
import me.androidbox.core.presentation.designsystem.components.PasswordRequirement

@OptIn(ExperimentalFoundationApi::class)
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

                val annotatedString = buildDescriptionAnnotatedString()
                ClickableText(text = annotatedString) { offSet ->
                    annotatedString.getStringAnnotations(
                        tag = "clickable_text",
                        start = offSet,
                        end = offSet
                    ).firstOrNull()?.let {
                        onAction(RegisterAction.OnLoginClicked)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                BusbyRunnerTextField(
                    state = registerState.email,
                    startIcon = EmailIcon,
                    endIcon = if(registerState.isValidEmail) CheckIcon else null,
                    hint = stringResource(id = R.string.example_email),
                    title = stringResource(id = R.string.email),
                    modifier = Modifier.fillMaxWidth(),
                    additionalInfo = stringResource(id = R.string.must_be_valid_email),
                    keyboardType = KeyboardType.Email
                )

                BusbyRunnerPasswordTextField(
                    state = registerState.password,
                    isPasswordVisible = registerState.isPasswordVisible,
                    hint = stringResource(id = R.string.password),
                    title = stringResource(id = R.string.password),
                    modifier = Modifier.fillMaxWidth(),
                    onTogglePasswordVisibility = {
                        onAction(RegisterAction.OnTogglePasswordVisibilityClicked)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordRequirement(
                    isValid = registerState.passwordValidationState.isValidPassword,
                    text = stringResource(id = R.string.password_length, UserDataValidator.MIN_PASSWORD_LENGTH),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                PasswordRequirement(
                    isValid = registerState.passwordValidationState.hasNumber,
                    text = stringResource(id = R.string.contain_number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                PasswordRequirement(
                    isValid = registerState.passwordValidationState.hasLowerCaseCharacter,
                    text = stringResource(id = R.string.lower_case_character),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

                PasswordRequirement(
                    isValid = registerState.passwordValidationState.hasUpperCaseCharacter,
                    text = stringResource(id = R.string.upper_case_character),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                BusbyRunnerActionButton(
                    text = stringResource(id = R.string.register),
                    isLoading = registerState.isRegistering,
                    isEnabled = true,
                    modifier = Modifier.fillMaxWidth(),
                    onClicked = {
                        onAction(RegisterAction.OnRegisterClicked)
                    }
                )
            }
        }
    )
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
    return annotatedString
}

@Composable
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun PreviewRegisterScreen() {
    BusbyRunnerTheme {
        RegisterScreen(
            registerState = RegisterState(
                passwordValidationState = PasswordValidationState(
                    true, true, true, true)
            ),
            onAction = {} )
    }
}