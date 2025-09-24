package com.maksimowiczm.foodyou.common.result

import com.maksimowiczm.foodyou.common.result.Result.Failure
import com.maksimowiczm.foodyou.common.result.Result.Success

sealed interface Result<out R, out E> {

    data class Success<out R, out E>(val data: R) : Result<R, E>

    data class Failure<out R, out E>(val error: E) : Result<R, E>
}

inline fun <R, E, T> Result<R, E>.fold(onSuccess: (R) -> T, onFailure: (E) -> T): T =
    when (this) {
        is Success -> onSuccess(data)
        is Failure -> onFailure(error)
    }

inline fun <R, E> Result<R, E>.consume(onSuccess: (R) -> Unit = {}, onFailure: (E) -> Unit = {}) =
    when (this) {
        is Success -> onSuccess(data)
        is Failure -> onFailure(error)
    }

inline fun <R, E, T> Result<R, E>.onSuccess(action: (R) -> Unit): Result<R, E> {
    if (this is Success) {
        action(data)
    }

    return this
}

inline fun <R, E, T> Result<R, E>.onFailure(action: (E) -> Unit): Result<R, E> {
    if (this is Failure) {
        action(error)
    }

    return this
}

@Suppress("FunctionName") fun <R, E> Ok(data: R): Result<R, E> = Result.Success(data)

@Suppress("FunctionName") fun <R, E> Err(error: E): Result<R, E> = Result.Failure(error)
