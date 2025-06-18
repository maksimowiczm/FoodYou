package com.maksimowiczm.foodyou.feature.diary.product.domain

internal fun interface RemoteProductMatcher {
    fun matches(url: String): Boolean
}
