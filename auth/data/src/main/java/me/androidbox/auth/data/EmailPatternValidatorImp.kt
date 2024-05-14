package me.androidbox.auth.data

import android.util.Patterns
import me.androidbox.auth.domain.PatternValidator

object EmailPatternValidatorImp : PatternValidator {

    override fun matches(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}