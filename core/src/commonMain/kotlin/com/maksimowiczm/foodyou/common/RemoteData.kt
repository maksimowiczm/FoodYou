package com.maksimowiczm.foodyou.common

sealed interface RemoteData<out T> {
    data class Loading<out T>(val partialValue: T?) : RemoteData<T>

    data class Success<out T>(val value: T) : RemoteData<T>

    data class Error<out T>(val error: Throwable, val partialValue: T?) : RemoteData<T>

    data object NotFound : RemoteData<Nothing>

    companion object {
        fun <T> fromNullable(value: T?): RemoteData<T> =
            when (value) {
                null -> NotFound
                else -> Success(value)
            }
    }
}

fun <T> RemoteData<T>.getOrNull(): T? =
    when (this) {
        is RemoteData.Success -> value
        is RemoteData.Error -> partialValue
        is RemoteData.Loading -> partialValue
        RemoteData.NotFound -> null
    }
