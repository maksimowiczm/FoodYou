package com.maksimowiczm.foodyou.feature.productdownload.domain

fun interface RemoteProductMatcher {
    fun matches(url: String): Boolean
}
