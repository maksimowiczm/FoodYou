package com.maksimowiczm.foodyou.foodsearch.domain

/**
 * Parser for converting raw search input into typed SearchQuery instances.
 *
 * Automatically detects the query type based on the input format:
 * - Blank/null input → SearchQuery.Blank
 * - All digits → SearchQuery.Barcode
 * - Open Food Facts URL → SearchQuery.OpenFoodFactsUrl
 * - FoodData Central URL → SearchQuery.FoodDataCentralUrl
 * - Everything else → SearchQuery.Text
 */
class SearchQueryParser {
    /**
     * Parses a raw query string into a typed SearchQuery.
     *
     * The input is trimmed before parsing to handle leading/trailing whitespace.
     *
     * @param query The raw search input from the user
     * @return A typed SearchQuery instance based on the detected format
     */
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
