package com.maksimowiczm.foodyou.food.search.domain

class SearchQueryParser {
    fun parse(query: String?): SearchQuery =
        when {
            query.isNullOrBlank() -> SearchQuery.Blank
            query.all(Char::isDigit) -> SearchQuery.Barcode(query)
            else -> SearchQuery.Text(query)
        }
}
