package com.maksimowiczm.foodyou.swissfoodcomposition.domain

import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import kotlin.jvm.JvmInline

@JvmInline value class SwissFoodCompositionProductId(val value: Long)

data class SwissFoodCompositionProduct(
    val id: SwissFoodCompositionProductId,
    val name: FoodName,
    val density: Double?,
    val energyKj: SwissFoodNutrientComponent<Energy>?,
    val energyKcal: SwissFoodNutrientComponent<Energy>?,
    val fatTotal: SwissFoodNutrientComponent<Weight>?,
    val fattyAcidsSaturated: SwissFoodNutrientComponent<Weight>?,
    val fattyAcidsMonounsaturated: SwissFoodNutrientComponent<Weight>?,
    val fattyAcidsPolyunsaturated: SwissFoodNutrientComponent<Weight>?,
    val linoleicAcid: SwissFoodNutrientComponent<Weight>?,
    val alphaLinolenicAcid: SwissFoodNutrientComponent<Weight>?,
    val eicosapentaenoicAcidEpa: SwissFoodNutrientComponent<Weight>?,
    val docosahexaenoicAcidDha: SwissFoodNutrientComponent<Weight>?,
    val cholesterol: SwissFoodNutrientComponent<Weight>?,
    val carbohydratesAvailable: SwissFoodNutrientComponent<Weight>?,
    val sugars: SwissFoodNutrientComponent<Weight>?,
    val starch: SwissFoodNutrientComponent<Weight>?,
    val dietaryFibres: SwissFoodNutrientComponent<Weight>?,
    val protein: SwissFoodNutrientComponent<Weight>?,
    val saltNacl: SwissFoodNutrientComponent<Weight>?,
    val alcohol: SwissFoodNutrientComponent<Weight>?,
    val water: SwissFoodNutrientComponent<Weight>?,
    val vitaminARe: SwissFoodNutrientComponent<Weight>?,
    val vitaminARae: SwissFoodNutrientComponent<Weight>?,
    val retinol: SwissFoodNutrientComponent<Weight>?,
    val betaCaroteneActivity: SwissFoodNutrientComponent<Weight>?,
    val betaCarotene: SwissFoodNutrientComponent<Weight>?,
    val vitaminB1Thiamine: SwissFoodNutrientComponent<Weight>?,
    val vitaminB2Riboflavin: SwissFoodNutrientComponent<Weight>?,
    val vitaminB6Pyridoxine: SwissFoodNutrientComponent<Weight>?,
    val vitaminB12Cobalamin: SwissFoodNutrientComponent<Weight>?,
    val niacinEquivalent: SwissFoodNutrientComponent<Weight>?,
    val niacin: SwissFoodNutrientComponent<Weight>?,
    val folate: SwissFoodNutrientComponent<Weight>?,
    val pantothenicAcid: SwissFoodNutrientComponent<Weight>?,
    val vitaminCAscorbicAcid: SwissFoodNutrientComponent<Weight>?,
    val vitaminDCalciferol: SwissFoodNutrientComponent<Weight>?,
    val vitaminETocopherol: SwissFoodNutrientComponent<Weight>?,
    val potassium: SwissFoodNutrientComponent<Weight>?,
    val sodium: SwissFoodNutrientComponent<Weight>?,
    val chloride: SwissFoodNutrientComponent<Weight>?,
    val calcium: SwissFoodNutrientComponent<Weight>?,
    val magnesium: SwissFoodNutrientComponent<Weight>?,
    val phosphorus: SwissFoodNutrientComponent<Weight>?,
    val iron: SwissFoodNutrientComponent<Weight>?,
    val iodide: SwissFoodNutrientComponent<Weight>?,
    val zinc: SwissFoodNutrientComponent<Weight>?,
    val selenium: SwissFoodNutrientComponent<Weight>?,
)
