package com.maksimowiczm.foodyou.core.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.lambda(block: suspend CoroutineScope.() -> Unit): () -> Unit = {
    launch {
        block()
    }
}
