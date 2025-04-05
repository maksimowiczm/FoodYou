package com.maksimowiczm.foodyou.ext

inline fun <T> List<T>.sumOf(selector: (T) -> Float) = fold(0f) { acc, element ->
    selector(element) + acc
}
