package com.maksimowiczm.foodyou.core.ext

inline fun <T> Iterable<T>.sumOf(selector: (T) -> Float) = fold(0f) { acc, element ->
    selector(element) + acc
}
