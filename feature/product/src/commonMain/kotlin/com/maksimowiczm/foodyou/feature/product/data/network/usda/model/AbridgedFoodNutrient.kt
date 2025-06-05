package com.maksimowiczm.foodyou.feature.product.data.network.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AbridgedFoodNutrient(
    @SerialName("number")
    val number: String,
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: Double,
    @SerialName("unitName")
    val unit: String
)
