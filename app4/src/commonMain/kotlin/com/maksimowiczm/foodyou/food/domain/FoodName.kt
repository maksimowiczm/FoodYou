package com.maksimowiczm.foodyou.food.domain

data class FoodName(
    val english: String? = null,
    val catalan: String? = null,
    val danish: String? = null,
    val german: String? = null,
    val spanish: String? = null,
    val french: String? = null,
    val italian: String? = null,
    val hungarian: String? = null,
    val dutch: String? = null,
    val polish: String? = null,
    val portugueseBrazil: String? = null,
    val turkish: String? = null,
    val russian: String? = null,
    val ukrainian: String? = null,
    val arabic: String? = null,
    val chineseSimplified: String? = null,
    val fallback: String,
) {
    private val list: List<String?> =
        listOf(
            english,
            catalan,
            danish,
            german,
            spanish,
            french,
            italian,
            hungarian,
            dutch,
            polish,
            portugueseBrazil,
            turkish,
            russian,
            ukrainian,
            arabic,
            chineseSimplified,
            fallback,
        )

    init {
        list.forEach {
            if (it != null) {
                require(it.isNotBlank()) { "Food name cannot be blank if provided" }
            }
        }
    }
}
