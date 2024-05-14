package me.androidbox.auth.data

import android.util.Patterns
import me.androidbox.auth.domain.PatternValidator

object EmailPatternValidatorImp : PatternValidator {

    override fun matchs(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}