package com.maksimowiczm.foodyou.feature.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NullableAbridgedFoodNutrient(
    @SerialName("number")
    val number: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("amount")
    val amount: Double? = null,
    @SerialName("unitName")
    val unit: String? = null
)
