package com.maksimowiczm.foodyou.app.ui.common.extension

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> LaunchedCollectWithLifecycle(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit,
) {
    val latestAction by rememberUpdatedState(action)

    LaunchedEffect(lifecycleOwner, flow) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) { flow.collect(latestAction) }
    }
}
