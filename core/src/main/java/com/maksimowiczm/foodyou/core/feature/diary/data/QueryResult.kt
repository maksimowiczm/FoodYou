package com.maksimowiczm.foodyou.core.feature.diary.data

data class QueryResult<T>(val isLoading: Boolean, val error: Throwable?, val data: T) {
    companion object {
        fun <T> loading(data: T) = QueryResult(
            isLoading = true,
            error = null,
            data = data
        )

        fun <T> success(data: T) = QueryResult(
            isLoading = false,
            error = null,
            data = data
        )

        fun <T> error(error: Throwable, data: T) = QueryResult(
            isLoading = false,
            error = error,
            data = data
        )
    }
}
