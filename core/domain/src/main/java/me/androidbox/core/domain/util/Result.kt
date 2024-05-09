package me.androidbox.core.domain.util

sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Failure<out E: Error>(val error: E): Result<Nothing, E>
}

inline fun <D, E : Error, R> Result<D, E>.mapper(map: (D) -> R): Result<R, E> {
    return when(this) {
        is Result.Failure -> {
            Result.Failure(this.error)
        }
        is Result.Success -> {
            Result.Success(map(this.data))
        }
    }
}

fun <D, E: Error> Result<D, E>.asEmptyDataResult(): EmptyDataResult<E> {
    return mapper {}
}

typealias EmptyDataResult<E> = Result<Unit, E>