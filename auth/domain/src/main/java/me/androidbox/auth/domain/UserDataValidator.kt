package me.androidbox.auth.domain

class UserDataValidator(
    private val patternValidator: PatternValidator) {

    fun isValidEmail(email: String): Boolean {
        return patternValidator.matchs(email.trim())
    }

    fun validatePassword(password: String): PasswordValidationState {
        val isMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasNumber = password.any { it.isDigit() }
        val hasLowerCaseCharacter = password.any { it.isLowerCase() }
        val hasUpperCaseCharacter = password.any { it.isUpperCase() }

        return PasswordValidationState(
            hasMinLength = isMinLength,
            hasNumber = hasNumber,
            hasLowerCaseCharacter = hasLowerCaseCharacter,
            hasUpperCaseCharacter = hasUpperCaseCharacter)
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 9
    }
}
