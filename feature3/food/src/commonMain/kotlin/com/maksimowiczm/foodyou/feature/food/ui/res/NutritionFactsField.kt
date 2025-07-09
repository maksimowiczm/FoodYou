package com.maksimowiczm.foodyou.feature.food.ui.res

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField.*
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutritionFactsField.stringResource(): String = when (this) {
    Proteins -> stringResource(Res.string.nutriment_proteins)
    Carbohydrates -> stringResource(Res.string.nutriment_carbohydrates)
    Fats -> stringResource(Res.string.nutriment_fats)
    Energy -> stringResource(Res.string.unit_energy)
    SaturatedFats -> stringResource(Res.string.nutriment_saturated_fats)
    MonounsaturatedFats -> stringResource(Res.string.nutriment_monounsaturated_fats)
    PolyunsaturatedFats -> stringResource(Res.string.nutriment_polyunsaturated_fats)
    Omega3 -> stringResource(Res.string.nutriment_omega_3)
    Omega6 -> stringResource(Res.string.nutriment_omega_6)
    Sugars -> stringResource(Res.string.nutriment_sugars)
    Salt -> stringResource(Res.string.nutriment_salt)
    DietaryFiber -> stringResource(Res.string.nutriment_fiber)
    Cholesterol -> stringResource(Res.string.nutriment_cholesterol)
    Caffeine -> stringResource(Res.string.nutriment_caffeine)
    VitaminA -> stringResource(Res.string.vitamin_a)
    VitaminB1 -> stringResource(Res.string.vitamin_b1)
    VitaminB2 -> stringResource(Res.string.vitamin_b2)
    VitaminB3 -> stringResource(Res.string.vitamin_b3)
    VitaminB5 -> stringResource(Res.string.vitamin_b5)
    VitaminB6 -> stringResource(Res.string.vitamin_b6)
    VitaminB7 -> stringResource(Res.string.vitamin_b7)
    VitaminB9 -> stringResource(Res.string.vitamin_b9)
    VitaminB12 -> stringResource(Res.string.vitamin_b12)
    VitaminC -> stringResource(Res.string.vitamin_c)
    VitaminD -> stringResource(Res.string.vitamin_d)
    VitaminE -> stringResource(Res.string.vitamin_e)
    VitaminK -> stringResource(Res.string.vitamin_k)
    Manganese -> stringResource(Res.string.mineral_manganese)
    Magnesium -> stringResource(Res.string.mineral_magnesium)
    Potassium -> stringResource(Res.string.mineral_potassium)
    Calcium -> stringResource(Res.string.mineral_calcium)
    Copper -> stringResource(Res.string.mineral_copper)
    Zinc -> stringResource(Res.string.mineral_zinc)
    Sodium -> stringResource(Res.string.mineral_sodium)
    Iron -> stringResource(Res.string.mineral_iron)
    Phosphorus -> stringResource(Res.string.mineral_phosphorus)
    Selenium -> stringResource(Res.string.mineral_selenium)
    Iodine -> stringResource(Res.string.mineral_iodine)
    Chromium -> stringResource(Res.string.mineral_chromium)
    NutritionFactsField.TransFats -> stringResource(Res.string.nutriment_trans_fats)
    NutritionFactsField.AddedSugars -> stringResource(Res.string.nutriment_added_sugars)
    NutritionFactsField.SolubleFiber -> stringResource(Res.string.nutriment_soluble_fiber)
    NutritionFactsField.InsolubleFiber -> stringResource(Res.string.nutriment_insoluble_fiber)
}
