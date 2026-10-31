package com.maksimowiczm.foodyou.swissfoodcomposition.domain

import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.TrackedLeafFoodSnapshot
import kotlinx.serialization.Serializable

@Serializable @JvmInline value class SwissFoodCompositionProductId(val value: Long)

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

val SwissFoodCompositionProduct.nutritionFacts: NutritionFacts
    get() =
        NutritionFacts(
            proteins = protein?.value ?: NutrientValue.Incomplete(null),
            carbohydrates = carbohydratesAvailable?.value ?: NutrientValue.Incomplete(null),
            energy = energyKcal?.value ?: NutrientValue.Incomplete(null),
            fats = fatTotal?.value ?: NutrientValue.Incomplete(null),
            saturatedFats = fattyAcidsSaturated?.value ?: NutrientValue.Incomplete(null),
            monounsaturatedFats =
                fattyAcidsMonounsaturated?.value ?: NutrientValue.Incomplete(null),
            polyunsaturatedFats =
                fattyAcidsPolyunsaturated?.value ?: NutrientValue.Incomplete(null),
            sugars = sugars?.value ?: NutrientValue.Incomplete(null),
            dietaryFiber = dietaryFibres?.value ?: NutrientValue.Incomplete(null),
            salt = saltNacl?.value ?: NutrientValue.Incomplete(null),
            cholesterol = cholesterol?.value ?: NutrientValue.Incomplete(null),
            vitaminA = vitaminARae?.value ?: vitaminARe?.value ?: NutrientValue.Incomplete(null),
            vitaminB1 = vitaminB1Thiamine?.value ?: NutrientValue.Incomplete(null),
            vitaminB2 = vitaminB2Riboflavin?.value ?: NutrientValue.Incomplete(null),
            vitaminB3 = niacinEquivalent?.value ?: niacin?.value ?: NutrientValue.Incomplete(null),
            vitaminB5 = pantothenicAcid?.value ?: NutrientValue.Incomplete(null),
            vitaminB6 = vitaminB6Pyridoxine?.value ?: NutrientValue.Incomplete(null),
            vitaminB9 = folate?.value ?: NutrientValue.Incomplete(null),
            vitaminB12 = vitaminB12Cobalamin?.value ?: NutrientValue.Incomplete(null),
            vitaminC = vitaminCAscorbicAcid?.value ?: NutrientValue.Incomplete(null),
            vitaminD = vitaminDCalciferol?.value ?: NutrientValue.Incomplete(null),
            vitaminE = vitaminETocopherol?.value ?: NutrientValue.Incomplete(null),
            potassium = potassium?.value ?: NutrientValue.Incomplete(null),
            sodium = sodium?.value ?: NutrientValue.Incomplete(null),
            calcium = calcium?.value ?: NutrientValue.Incomplete(null),
            magnesium = magnesium?.value ?: NutrientValue.Incomplete(null),
            phosphorus = phosphorus?.value ?: NutrientValue.Incomplete(null),
            iron = iron?.value ?: NutrientValue.Incomplete(null),
            iodine = iodide?.value ?: NutrientValue.Incomplete(null),
            zinc = zinc?.value ?: NutrientValue.Incomplete(null),
            selenium = selenium?.value ?: NutrientValue.Incomplete(null),
        )

fun SwissFoodCompositionProduct.toSnapshot() =
    TrackedLeafFoodSnapshot(
        id = FoodSnapshotId.SwissFoodComposition(id.value),
        name = name,
        brand = null,
        image = null,
        nutritionFacts = nutritionFacts,
    )

fun FoodSnapshotId.SwissFoodComposition.toSwissFoodCompositionProductId() =
    SwissFoodCompositionProductId(id)
