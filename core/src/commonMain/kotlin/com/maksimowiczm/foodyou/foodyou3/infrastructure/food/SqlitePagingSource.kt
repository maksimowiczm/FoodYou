package com.maksimowiczm.foodyou.foodyou3.infrastructure.food

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.ktor.utils.io.CancellationException

internal class SqlitePagingSource<T : Any>(
    private val query: (limit: Int, offset: Int) -> List<T>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return maxOf(0, anchorPosition - (state.config.pageSize / 2))
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val currentOffset = params.key ?: 0
        val limit = params.loadSize
        return try {
            val data = query(limit, currentOffset)
            val prevKey = if (currentOffset <= 0) null else maxOf(0, currentOffset - limit)
            val nextKey = if (data.size < limit) null else currentOffset + data.size
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
