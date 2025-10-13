package com.maksimowiczm.foodyou.food.domain

data class FoodName(
    val english: String?,
    val catalan: String?,
    val danish: String?,
    val german: String?,
    val spanish: String?,
    val french: String?,
    val italian: String?,
    val hungarian: String?,
    val dutch: String?,
    val polish: String?,
    val portugueseBrazil: String?,
    val turkish: String?,
    val russian: String?,
    val ukrainian: String?,
    val arabic: String?,
    val chineseSimplified: String?,
) {
    init {
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
            )
            .forEach {
                if (it != null) {
                    require(it.isNotBlank()) { "Food name cannot be blank if provided" }
                }
            }

        require(
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
                )
                .any { !it.isNullOrBlank() }
        ) {
            "At least one food name must be provided"
        }
    }
}
