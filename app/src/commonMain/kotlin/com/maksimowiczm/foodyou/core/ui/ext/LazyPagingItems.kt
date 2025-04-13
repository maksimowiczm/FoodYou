package com.maksimowiczm.foodyou.core.ui.ext

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

val LazyPagingItems<*>.throwable: Throwable?
    get() {
        when (val state = loadState.refresh) {
            is LoadState.Error -> return state.error
            else -> Unit
        }

        when (val state = loadState.append) {
            is LoadState.Error -> return state.error
            else -> Unit
        }

        when (val state = loadState.prepend) {
            is LoadState.Error -> return state.error
            else -> Unit
        }

        return null
    }
