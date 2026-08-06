package com.maksimowiczm.foodyou.common.domain.search

import kotlinx.serialization.Serializable

/**
 * Sealed interface representing different types of food product search queries.
 *
 * Supports various query formats including text search, barcode lookup, and URL parsing for Open
 * Food Facts and FoodData Central.
 */
@Serializable
sealed interface SearchQuery {
    /** The raw query string, or null for blank queries. */
    val query: String?

    /**
     * Represents an empty or blank search query.
     *
     * Used when no search term is provided.
     */
    @Serializable
    data object Blank : SearchQuery {
        override val query: String? = null
    }

    /**
     * Base interface for non-blank search queries.
     *
     * Guarantees that the query string is non-null.
     */
    interface NotBlank : SearchQuery {
        override val query: String
    }

    /**
     * Search query using a product barcode.
     *
     * @property barcode The product barcode
     */
    @Serializable
    data class Barcode(val barcode: String) : NotBlank {
        override val query: String = barcode

        companion object {
            val recognizer = SearchQueryRecognizer { query ->
                if (query.isNotEmpty() && query.all(Char::isDigit)) Barcode(query) else null
            }
        }
    }

    /**
     * Search query using free-form text.
     *
     * @property query The search text
     */
    @Serializable data class Text(override val query: String) : NotBlank
}
