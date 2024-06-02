package me.androidbox.core.domain.util

sealed interface Result<out T, out E: Error> {
    data class Success<out T>(val data: T): Result<T, Nothing>
    data class Failure<out E: Error>(val error: E): Result<Nothing, E>
}

inline fun <T, E : Error, R> Result<T, E>.mapper(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Failure -> {
            Result.Failure(this.error)
        }
        is Result.Success -> {
            Result.Success(map(this.data))
        }
    }
}

fun <T, E: Error> Result<T, E>.asEmptyResult(): Result<Unit, E> {
    return mapper { }
}

typealias EmptyResult<E> = Result<Unit, E>