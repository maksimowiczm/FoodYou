package com.maksimowiczm.foodyou.food.search.domain

sealed interface SearchQuery {
    val query: String?

    data object Blank : SearchQuery {
        override val query: String? = null
    }

    sealed interface NotBlank : SearchQuery {
        override val query: String
    }

    data class Barcode(val barcode: String) : NotBlank {
        override val query: String = barcode
    }

    data class Text(override val query: String) : NotBlank
}
