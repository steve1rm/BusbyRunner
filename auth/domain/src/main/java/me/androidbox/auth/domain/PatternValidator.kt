package me.androidbox.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}