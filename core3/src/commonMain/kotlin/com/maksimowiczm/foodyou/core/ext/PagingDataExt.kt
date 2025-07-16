package com.maksimowiczm.foodyou.core.ext

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T : Any, R : Any> Flow<PagingData<T>>.mapData(
    transform: suspend (T) -> R
): Flow<PagingData<R>> = this.map { pagingData -> pagingData.map(transform) }
