package com.maksimowiczm.foodyou.core.ui.res

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.core.model.NutritionFactsField
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutritionFactsField.stringResource(): String = when (this) {
    NutritionFactsField.Proteins -> stringResource(Res.string.nutriment_proteins)
    NutritionFactsField.Carbohydrates -> stringResource(Res.string.nutriment_carbohydrates)
    NutritionFactsField.Fats -> stringResource(Res.string.nutriment_fats)
    NutritionFactsField.Energy -> stringResource(Res.string.unit_energy)
    NutritionFactsField.SaturatedFats -> stringResource(Res.string.nutriment_saturated_fats)
    NutritionFactsField.MonounsaturatedFats ->
        stringResource(Res.string.nutriment_monounsaturated_fats)

    NutritionFactsField.PolyunsaturatedFats ->
        stringResource(Res.string.nutriment_polyunsaturated_fats)

    NutritionFactsField.Omega3 -> stringResource(Res.string.nutriment_omega_3)
    NutritionFactsField.Omega6 -> stringResource(Res.string.nutriment_omega_6)
    NutritionFactsField.Sugars -> stringResource(Res.string.nutriment_sugars)
    NutritionFactsField.Salt -> stringResource(Res.string.nutriment_salt)
    NutritionFactsField.Fiber -> stringResource(Res.string.nutriment_fiber)
    NutritionFactsField.Cholesterol -> stringResource(Res.string.nutriment_cholesterol)
    NutritionFactsField.Caffeine -> stringResource(Res.string.nutriment_caffeine)
    NutritionFactsField.VitaminA -> stringResource(Res.string.vitamin_a)
    NutritionFactsField.VitaminB1 -> stringResource(Res.string.vitamin_b1)
    NutritionFactsField.VitaminB2 -> stringResource(Res.string.vitamin_b2)
    NutritionFactsField.VitaminB3 -> stringResource(Res.string.vitamin_b3)
    NutritionFactsField.VitaminB5 -> stringResource(Res.string.vitamin_b5)
    NutritionFactsField.VitaminB6 -> stringResource(Res.string.vitamin_b6)
    NutritionFactsField.VitaminB7 -> stringResource(Res.string.vitamin_b7)
    NutritionFactsField.VitaminB9 -> stringResource(Res.string.vitamin_b9)
    NutritionFactsField.VitaminB12 -> stringResource(Res.string.vitamin_b12)
    NutritionFactsField.VitaminC -> stringResource(Res.string.vitamin_c)
    NutritionFactsField.VitaminD -> stringResource(Res.string.vitamin_d)
    NutritionFactsField.VitaminE -> stringResource(Res.string.vitamin_e)
    NutritionFactsField.VitaminK -> stringResource(Res.string.vitamin_k)
    NutritionFactsField.Manganese -> stringResource(Res.string.mineral_manganese)
    NutritionFactsField.Magnesium -> stringResource(Res.string.mineral_magnesium)
    NutritionFactsField.Potassium -> stringResource(Res.string.mineral_potassium)
    NutritionFactsField.Calcium -> stringResource(Res.string.mineral_calcium)
    NutritionFactsField.Copper -> stringResource(Res.string.mineral_copper)
    NutritionFactsField.Zinc -> stringResource(Res.string.mineral_zinc)
    NutritionFactsField.Sodium -> stringResource(Res.string.mineral_sodium)
    NutritionFactsField.Iron -> stringResource(Res.string.mineral_iron)
    NutritionFactsField.Phosphorus -> stringResource(Res.string.mineral_phosphorus)
    NutritionFactsField.Selenium -> stringResource(Res.string.mineral_selenium)
    NutritionFactsField.Iodine -> stringResource(Res.string.mineral_iodine)
    NutritionFactsField.Chromium -> stringResource(Res.string.mineral_chromium)
}
