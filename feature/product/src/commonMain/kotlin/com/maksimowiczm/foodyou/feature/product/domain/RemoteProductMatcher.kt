package com.maksimowiczm.foodyou.feature.product.domain

internal fun interface RemoteProductMatcher {
    fun matches(url: String): Boolean
}
