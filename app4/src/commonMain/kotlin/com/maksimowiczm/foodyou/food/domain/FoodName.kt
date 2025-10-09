package com.maksimowiczm.foodyou.food.domain

data class FoodName(
    val english: String,
    val german: String?,
    val french: String?,
    val italian: String?,
) {
    init {
        require(english.isNotBlank()) { "Food name in English cannot be blank" }

        listOfNotNull(german, french, italian).forEach {
            require(it.isNotBlank()) { "Food name cannot be blank if provided" }
        }
    }
}
