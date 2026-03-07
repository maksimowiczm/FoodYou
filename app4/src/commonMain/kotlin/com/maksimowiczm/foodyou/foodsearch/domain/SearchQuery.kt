package com.maksimowiczm.foodyou.foodsearch.domain

/**
 * Sealed interface representing different types of food product search queries.
 *
 * Supports various query formats including text search, barcode lookup, and URL parsing for Open
 * Food Facts and FoodData Central.
 */
sealed interface SearchQuery {
    /** The raw query string, or null for blank queries. */
    val query: String?

    /**
     * Represents an empty or blank search query.
     *
     * Used when no search term is provided.
     */
    data object Blank : SearchQuery {
        override val query: String? = null
    }

    /**
     * Base interface for non-blank search queries.
     *
     * Guarantees that the query string is non-null.
     */
    sealed interface NotBlank : SearchQuery {
        override val query: String
    }

    /**
     * Search query using a product barcode.
     *
     * @property barcode The product barcode
     */
    data class Barcode(val barcode: String) : NotBlank {
        override val query: String = barcode
    }

    /**
     * Search query using free-form text.
     *
     * @property query The search text
     */
    data class Text(override val query: String) : NotBlank

    /**
     * Search query parsed from an Open Food Facts URL.
     *
     * Extracts the barcode from URLs like:
     * https://world.openfoodfacts.org/product/5449000000996/coca-cola
     *
     * @property url The Open Food Facts product URL
     * @property barcode The extracted product barcode
     * @throws IllegalStateException if the URL format is invalid
     */
    data class OpenFoodFactsUrl(val url: String) : NotBlank {
        override val query: String = url

        val barcode: String =
            url.substringAfterLast("/product/").substringBefore("/").takeIf {
                it.all(Char::isDigit)
            } ?: error("Invalid OpenFoodFacts URL: $url")

        companion object {
            /** Regex pattern for matching Open Food Facts product URLs. */
            val regex =
                "https://\\w+\\.openfoodfacts\\.org/product/(?<barcode>\\d+)(?:/.+)?".toRegex()
        }
    }

    /**
     * Search query parsed from a FoodData Central URL.
     *
     * Extracts the FDC ID from URLs like: https://fdc.nal.usda.gov/food-details/123456/nutrients
     *
     * @property url The FoodData Central product URL
     * @property fdcId The extracted FoodData Central ID
     * @throws IllegalStateException if the URL format is invalid
     */
    data class FoodDataCentralUrl(val url: String) : NotBlank {
        override val query: String = url

        val fdcId: Int =
            url.substringAfterLast("/food-details/").substringBefore("/").toIntOrNull()
                ?: error("Invalid FoodDataCentral URL: $url")

        companion object {
            /** Regex pattern for matching FoodData Central product URLs. */
            val regex =
                "https://fdc\\.nal\\.usda\\.gov/food-details/(?<fdcId>\\d+)(/nutrients)?".toRegex()
        }
    }
}
