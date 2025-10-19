package com.maksimowiczm.foodyou.food.search.domain

class SearchQueryParser {
    fun parse(query: String?): SearchQuery = internalParse(query?.trim())

    private fun internalParse(query: String?): SearchQuery =
        when {
            query.isNullOrBlank() -> SearchQuery.Blank
            query.all(Char::isDigit) -> SearchQuery.Barcode(query)
            else -> {
                val openFoodFactsMatch = SearchQuery.OpenFoodFactsUrl.regex.find(query)
                when {
                    openFoodFactsMatch != null -> {
                        SearchQuery.OpenFoodFactsUrl(openFoodFactsMatch.value)
                    }

                    else -> SearchQuery.Text(query)
                }
            }
        }
}
