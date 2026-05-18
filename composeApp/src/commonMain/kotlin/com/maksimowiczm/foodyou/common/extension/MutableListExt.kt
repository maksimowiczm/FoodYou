package com.maksimowiczm.foodyou.common.extension

fun <T> MutableList<T>.removeLastIf(predicate: (T) -> Boolean) {
    if (isNotEmpty() && predicate(last())) {
        removeAt(lastIndex)
    }
}

inline fun <reified T> MutableList<*>.removeLastIf(): Unit = removeLastIf { it is T }

// Must use this because Android might not have virtual method removeLast
fun MutableList<*>.safeRemoveLast() = removeAt(lastIndex)
