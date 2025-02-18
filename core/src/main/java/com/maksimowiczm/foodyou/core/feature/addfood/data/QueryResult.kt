package com.maksimowiczm.foodyou.core.feature.addfood.data

data class QueryResult<T>(
    val isLoading: Boolean,
    val error: Throwable?,
    private val data: List<T>,
    val size: Int = data.size
) {
    fun get(index: Int) = data.getOrNull(index)

    fun isEmpty() = size == 0

    companion object {
        fun <T> loading(
            data: List<T>,
            size: Int = data.size
        ) = QueryResult(
            isLoading = true,
            error = null,
            data = data,
            size = size
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
