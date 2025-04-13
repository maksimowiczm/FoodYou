package com.maksimowiczm.foodyou.core.ext

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T : Any, R : Any> Flow<PagingData<T>>.mapValues(
    crossinline transform: suspend (value: T) -> R
): Flow<PagingData<R>> = this.map { it.map { transform(it) } }
