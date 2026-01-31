package com.maksimowiczm.foodyou.common.domain

sealed interface RemoteData<out T> {
    data class Loading<out T>(val partialValue: T?) : RemoteData<T>

    data class Success<out T>(val value: T) : RemoteData<T>

    data class Error<out T>(val error: Throwable, val partialValue: T?) : RemoteData<T>

    data object NotFound : RemoteData<Nothing>
}
