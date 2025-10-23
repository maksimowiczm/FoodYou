package com.maksimowiczm.foodyou.food.search.domain

class SearchQueryParser {
    fun parse(query: String?): SearchQuery = internalParse(query?.trim())

    private fun internalParse(query: String?): SearchQuery {
        when {
            query.isNullOrBlank() -> return SearchQuery.Blank
            query.all(Char::isDigit) -> return SearchQuery.Barcode(query)
            else -> {
                val openFoodFactsMatch = SearchQuery.OpenFoodFactsUrl.regex.find(query)
                if (openFoodFactsMatch != null) {
                    return SearchQuery.OpenFoodFactsUrl(openFoodFactsMatch.value)
                }

                val foodDataCentralMatch = SearchQuery.FoodDataCentralUrl.regex.find(query)
                if (foodDataCentralMatch != null) {
                    return SearchQuery.FoodDataCentralUrl(foodDataCentralMatch.value)
                }

                return SearchQuery.Text(query)
            }
        }
    }
}
