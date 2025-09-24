package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.runtime.*
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@Composable
@OptIn(FlowPreview::class)
internal fun <T : Any> LazyPagingItems<T>.delayedLoadingState(timeout: Long = 100L): Boolean {
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        snapshotFlow {
                loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
            }
            .debounce(timeout)
            .collectLatest { isLoading = it }
    }

    return isLoading
}
