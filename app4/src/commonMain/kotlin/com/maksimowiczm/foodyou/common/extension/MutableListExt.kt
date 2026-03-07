package com.maksimowiczm.foodyou.common.extension

fun <T> MutableList<T>.removeLastIf(predicate: (T) -> Boolean) {
    if (isNotEmpty() && predicate(last())) {
        removeAt(lastIndex)
    }
}

inline fun <reified T> MutableList<*>.removeLastIf(): Unit = removeLastIf { it is T }
