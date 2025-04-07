package com.maksimowiczm.foodyou.feature.diary.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OpenFoodFactsNutrients(
    @SerialName("carbohydrates_100g")
    val carbohydrates100g: Float? = null,
    @SerialName("energy-kcal_100g")
    val energy100g: Float? = null,
    @SerialName("fat_100g")
    val fat100g: Float? = null,
    @SerialName("proteins_100g")
    val proteins100g: Float? = null,
    @SerialName("salt_100g")
    val salt100g: Float? = null,
    @SerialName("saturated-fat_100g")
    val saturatedFat100g: Float? = null,
    @SerialName("sodium_100g")
    val sodium100g: Float? = null,
    @SerialName("sugars_100g")
    val sugars100g: Float? = null,
    @SerialName("fiber_100g")
    val fiber100g: Float? = null
)
