package com.maksimowiczm.foodyou.core.ui.ext

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

val LazyPagingItems<*>.throwable: Throwable?
    get() {
        when (val state = loadState.refresh) {
            is LoadState.Error -> return state.error
            else -> null
        }

        when (val state = loadState.append) {
            is LoadState.Error -> return state.error
            else -> null
        }

        when (val state = loadState.prepend) {
            is LoadState.Error -> return state.error
            else -> null
        }

        return null
    }
