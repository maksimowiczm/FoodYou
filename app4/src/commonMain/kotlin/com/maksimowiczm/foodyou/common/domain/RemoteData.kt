package com.maksimowiczm.foodyou.common.domain

sealed interface RemoteData<out T> {
    data class Loading<T>(val partialValue: T?) : RemoteData<T>

    data class Success<T>(val value: T) : RemoteData<T>

    data class Error<T>(val error: Throwable, val partialValue: T?) : RemoteData<T>

    data object NotFound : RemoteData<Nothing>
}
