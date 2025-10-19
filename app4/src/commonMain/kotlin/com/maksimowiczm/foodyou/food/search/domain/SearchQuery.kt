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

    data class OpenFoodFactsUrl(val url: String) : NotBlank {
        override val query: String = url

        val barcode: String =
            url.substringAfterLast("/product/").substringBefore(":/").takeIf {
                it.all(Char::isDigit)
            } ?: error("Invalid OpenFoodFacts URL: $url")

        companion object {
            val regex =
                "https://\\w+\\.openfoodfacts\\.org/product/\\d+(?<barcode>:/\\S+)?".toRegex()
        }
    }
}
