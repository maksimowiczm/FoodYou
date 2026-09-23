package com.maksimowiczm.foodyou.common.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

inline fun <reified T> Iterable<Flow<T>>.combine(): Flow<List<T>> = combine(this) { it.toList() }
