package com.maksimowiczm.foodyou.app.ui.common.extension

import androidx.compose.runtime.snapshotFlow
import androidx.paging.compose.LazyPagingItems
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
fun LazyPagingItems<*>.debounceIsIdle(timeout: Duration = 200.milliseconds): Flow<Boolean> =
    snapshotFlow { loadState.isIdle }.debounce(timeout)
