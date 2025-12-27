package com.maksimowiczm.foodyou.importexport.tbca.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for a single nutrient entry in TBCA data
 * Example: {"Componente": "Energia", "Unidades": "kcal", "Valor por 100g": "121"}
 */
@Serializable
data class TBCANutrientDto(
    @SerialName("Componente")
    val component: String,

    @SerialName("Unidades")
    val units: String,

    @SerialName("Valor por 100g")
    val valuePer100g: String
)
