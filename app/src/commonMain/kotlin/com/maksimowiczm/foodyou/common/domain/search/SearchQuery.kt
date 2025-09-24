package com.maksimowiczm.foodyou.common.domain.search

sealed interface SearchQuery {
    val query: String?

    data object Blank : SearchQuery {
        override val query: String? = null
    }

    sealed interface NotBlank : SearchQuery {
        override val query: String
    }

    data class Barcode(override val query: String) : NotBlank

    data class Text(override val query: String) : NotBlank
}

fun searchQuery(query: String?): SearchQuery =
    when {
        query.isNullOrBlank() -> SearchQuery.Blank
        query.all(Char::isDigit) -> SearchQuery.Barcode(query)
        else -> SearchQuery.Text(query)
    }
