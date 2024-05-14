package me.androidbox.auth.domain

interface PatternValidator {
    fun matchs(value: String): Boolean
}