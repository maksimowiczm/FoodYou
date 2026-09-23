package com.maksimowiczm.foodyou.common

import com.maksimowiczm.foodyou.common.Result.Error
import com.maksimowiczm.foodyou.common.Result.Success
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed interface Result<out R, out E> {

    data class Success<out R>(val data: R) : Result<R, Nothing>

    data class Error<out E>(val error: E) : Result<Nothing, E>
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

fun <R> Result<R, *>.getOrNull(): R? =
    when (this) {
        is Error<*> -> null
        is Success<R> -> data
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

fun <R> Result<R, *>.expect(message: String): R =
    when (this) {
        is Success -> data
        is Error -> error("Expected Result = $message\n Error = $error")
    }

inline fun <R, E, R1> Result<R, E>.fold(onSuccess: (R) -> R1, onError: (E) -> R1): R1 =
    when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(error)
    }

@Suppress("FunctionName") fun <R> Ok(data: R): Result<R, Nothing> = Success(data)

@Suppress("FunctionName") fun Ok(): Result<Unit, Nothing> = Success(Unit)

@Suppress("FunctionName") fun <E> Err(error: E): Result<Nothing, E> = Error(error)
