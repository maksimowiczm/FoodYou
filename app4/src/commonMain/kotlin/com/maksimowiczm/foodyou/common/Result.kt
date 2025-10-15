package com.maksimowiczm.foodyou.common

import com.maksimowiczm.foodyou.common.Result.Error
import com.maksimowiczm.foodyou.common.Result.Success
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed interface Result<out R, out E> {

    data class Success<out R, out E>(val data: R) : Result<R, E>

    data class Error<out R, out E>(val error: E) : Result<R, E>
}

@OptIn(ExperimentalContracts::class)
fun Result<*, *>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Success)
        returns(false) implies (this@isSuccess is Error)
    }

    return this is Success
}

@OptIn(ExperimentalContracts::class)
fun Result<*, *>.isError(): Boolean {
    contract {
        returns(true) implies (this@isError is Error)
        returns(false) implies (this@isError is Success)
    }

    return this is Error
}

inline fun <R, E> Result<R, E>.onSuccess(action: (R) -> Unit): Result<R, E> {
    if (this is Success) {
        action(data)
    }

    return this
}

inline fun <R, E> Result<R, E>.onError(action: (E) -> Unit): Result<R, E> {
    if (this is Error) {
        action(error)
    }

    return this
}

inline fun <R, E1, E2> Result<R, E1>.mapError(transform: (E1) -> E2): Result<R, E2> =
    when (this) {
        is Success -> Success(data)
        is Error -> Error(transform(error))
    }

inline fun <R, E, R2> Result<R, E>.map(transform: (R) -> R2): Result<R2, E> =
    when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(error)
    }

fun <R, E> Result<R, E>.expect(message: String): R =
    when (this) {
        is Success -> data
        is Error -> error(message)
    }

@Suppress("FunctionName") fun <R, E> Ok(data: R): Result<R, E> = Success(data)

@Suppress("FunctionName") fun <E> Ok(): Result<Unit, E> = Success(Unit)

@Suppress("FunctionName") fun <R, E> Err(error: E): Result<R, E> = Error(error)
