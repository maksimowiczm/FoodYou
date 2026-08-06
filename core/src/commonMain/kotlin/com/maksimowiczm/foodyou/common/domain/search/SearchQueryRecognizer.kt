package com.maksimowiczm.foodyou.common.domain.search

fun interface SearchQueryRecognizer {
    fun recognize(query: String): SearchQuery?
}
