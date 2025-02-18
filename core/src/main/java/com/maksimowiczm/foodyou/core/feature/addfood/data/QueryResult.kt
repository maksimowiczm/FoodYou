package com.maksimowiczm.foodyou.core.feature.addfood.data

data class QueryResult<T>(
    val isLoading: Boolean,
    val error: Throwable?,
    val data: List<T>
) {
    companion object {
        fun <T> loading(
            data: List<T>
        ) = QueryResult(
            isLoading = true,
            error = null,
            data = data
        )

        fun <T> success(data: List<T>) = QueryResult(
            isLoading = false,
            error = null,
            data = data
        )

        fun <T> error(error: Throwable, data: List<T>) = QueryResult(
            isLoading = false,
            error = error,
            data = data
        )
    }
}
