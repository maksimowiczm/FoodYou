package com.maksimowiczm.foodyou.importexport.tbca.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for a single food item in TBCA (Brazilian Food Composition Table) data
 *
 * Example JSON structure:
 * {
 *   "codigo": "C0113T",
 *   "classe": "Leguminosas e derivados",
 *   "descricao": "Orelha-de-padre, semente, seca, remolho em água, cozida...",
 *   "nutrientes": [
 *     {"Componente": "Energia", "Unidades": "kcal", "Valor por 100g": "121"},
 *     ...
 *   ]
 * }
 */
@Serializable
data class TBCAFoodDto(
    @SerialName("codigo")
    val code: String,

    @SerialName("classe")
    val foodClass: String,

    @SerialName("descricao")
    val description: String,

    @SerialName("nutrientes")
    val nutrients: List<TBCANutrientDto>
)
