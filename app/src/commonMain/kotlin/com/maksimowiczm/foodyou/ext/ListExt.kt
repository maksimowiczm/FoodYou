package com.maksimowiczm.foodyou.ext

inline fun <T> Iterable<T>.sumOf(selector: (T) -> Float) = fold(0f) { acc, element ->
    selector(element) + acc
}

inline fun <T> Array<T>.sumOf(selector: (T) -> Float) = fold(0f) { acc, element ->
    selector(element) + acc
}
