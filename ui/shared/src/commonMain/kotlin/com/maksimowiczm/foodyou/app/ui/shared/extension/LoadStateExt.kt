package com.maksimowiczm.foodyou.app.ui.shared.extension

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

/**
 * Returns the [Throwable] from the [LoadState.Error] or `null` if the [LoadState] is not an error.
 * If the [LoadState] is [LoadState.NotLoading] or [LoadState.Loading], it returns `null`.
 */
val LoadState.error: Throwable?
    get() =
        when (this) {
            is LoadState.Error -> error
            is LoadState.NotLoading -> null
            is LoadState.Loading -> null
        }

/**
 * Returns the first [LoadState.Error] from the [CombinedLoadStates] or `null` if there are no
 * errors. If there are multiple errors, the first one encountered in the order of refresh, prepend,
 * and append
 */
val CombinedLoadStates.error: Throwable?
    get() =
        when (val refresh = refresh) {
            is LoadState.Error -> refresh.error
            is LoadState.NotLoading -> null
            is LoadState.Loading -> null
        }
            ?: when (val prepend = prepend) {
                is LoadState.Error -> prepend.error
                is LoadState.NotLoading -> null
                is LoadState.Loading -> null
            }
            ?: when (val append = append) {
                is LoadState.Error -> append.error
                is LoadState.NotLoading -> null
                is LoadState.Loading -> null
            }
