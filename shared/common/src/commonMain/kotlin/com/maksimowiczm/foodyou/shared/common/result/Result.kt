package com.maksimowiczm.foodyou.shared.common.result

sealed interface Result<out R, out E> {

    data class Success<out R, out E>(val data: R) : Result<R, E>

    data class Failure<out R, out E>(val error: E) : Result<R, E>

    suspend fun <T> fold(onSuccess: suspend (R) -> T, onFailure: suspend (E) -> T): T =
        when (this) {
            is Success -> onSuccess(data)
            is Failure -> onFailure(error)
        }

    suspend fun consume(onSuccess: suspend (R) -> Unit = {}, onFailure: suspend (E) -> Unit = {}) =
        when (this) {
            is Success -> onSuccess(data)
            is Failure -> onFailure(error)
        }

    suspend fun onSuccess(action: suspend (R) -> Unit): Result<R, E> {
        if (this is Success) {
            action(data)
        }

        return this
    }

    suspend fun onFailure(action: suspend (E) -> Unit): Result<R, E> {
        if (this is Failure) {
            action(error)
        }

        return this
    }
}

@Suppress("FunctionName") fun <R, E> Ok(data: R): Result<R, E> = Result.Success(data)

@Suppress("FunctionName") fun <R, E> Err(error: E): Result<R, E> = Result.Failure(error)
