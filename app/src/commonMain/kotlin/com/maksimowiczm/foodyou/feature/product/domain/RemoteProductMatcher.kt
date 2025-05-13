package com.maksimowiczm.foodyou.feature.product.domain

fun interface RemoteProductMatcher {
    fun matches(url: String): Boolean
}
