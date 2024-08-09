package me.androidbox.wear.run.presentation

import me.androidbox.core.presentation.ui.UiText
import me.androidbox.wear.run.domain.ExerciseError

fun ExerciseError.toUiText(): UiText? {
    return when(this) {
        ExerciseError.ONGOING_OWN_EXERCISE,
        ExerciseError.ONGOING_OTHER_EXERCISE -> {
            UiText.StringResource(R.string.error_on_going_exercise)
        }
        ExerciseError.EXERCISE_ALREADY_ENDED -> {
            UiText.StringResource(R.string.error_already_ended )
        }
        ExerciseError.UNKNOWN -> {
            UiText.StringResource(me.androidbox.core.presentation.ui.R.string.error_unknown)
        }
        ExerciseError.TRACKING_NOT_SUPPORTED -> null
    }
}