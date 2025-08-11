package com.maksimowiczm.foodyou.business.food.domain

sealed interface QueryType {
    val query: String?

    data object Blank : QueryType {
        override val query: String? = null
    }

    sealed interface NotBlank : QueryType {
        override val query: String

        data class Barcode(override val query: String) : NotBlank

        data class Text(override val query: String) : NotBlank
    }
}

fun queryType(query: String?): QueryType =
    when {
        query.isNullOrBlank() -> QueryType.Blank
        query.all { it.isDigit() } -> QueryType.NotBlank.Barcode(query)
        else -> QueryType.NotBlank.Text(query)
    }
