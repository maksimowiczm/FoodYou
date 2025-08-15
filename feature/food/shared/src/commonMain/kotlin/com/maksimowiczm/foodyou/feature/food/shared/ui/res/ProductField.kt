package com.maksimowiczm.foodyou.feature.food.shared.ui.res

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.business.food.domain.ProductField
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProductField.stringResource(): String =
    when (this) {
        ProductField.Name -> stringResource(Res.string.product_name)
        ProductField.Brand -> stringResource(Res.string.product_brand)
        ProductField.Barcode -> stringResource(Res.string.product_barcode)
        ProductField.Note -> stringResource(Res.string.headline_note)
        ProductField.IsLiquid -> stringResource(Res.string.action_treat_as_liquid)
        ProductField.PackageWeight -> stringResource(Res.string.product_package_weight)
        ProductField.ServingWeight -> stringResource(Res.string.product_serving_weight)
        ProductField.SourceUrl -> stringResource(Res.string.headline_source)
        ProductField.Proteins -> stringResource(Res.string.nutriment_proteins)
        ProductField.Carbohydrates -> stringResource(Res.string.nutriment_carbohydrates)
        ProductField.Energy -> stringResource(Res.string.unit_energy)
        ProductField.Fats -> stringResource(Res.string.nutriment_fats)
        ProductField.SaturatedFats -> stringResource(Res.string.nutriment_saturated_fats)
        ProductField.TransFats -> stringResource(Res.string.nutriment_trans_fats)
        ProductField.MonounsaturatedFats ->
            stringResource(Res.string.nutriment_monounsaturated_fats)
        ProductField.PolyunsaturatedFats ->
            stringResource(Res.string.nutriment_polyunsaturated_fats)
        ProductField.Omega3 -> stringResource(Res.string.nutriment_omega_3)
        ProductField.Omega6 -> stringResource(Res.string.nutriment_omega_6)
        ProductField.Sugars -> stringResource(Res.string.nutriment_sugars)
        ProductField.AddedSugars -> stringResource(Res.string.nutriment_added_sugars)
        ProductField.DietaryFiber -> stringResource(Res.string.nutriment_fiber)
        ProductField.SolubleFiber -> stringResource(Res.string.nutriment_soluble_fiber)
        ProductField.InsolubleFiber -> stringResource(Res.string.nutriment_insoluble_fiber)
        ProductField.Salt -> stringResource(Res.string.nutriment_salt)
        ProductField.Cholesterol -> stringResource(Res.string.nutriment_cholesterol)
        ProductField.Caffeine -> stringResource(Res.string.nutriment_caffeine)
        ProductField.VitaminA -> stringResource(Res.string.vitamin_a)
        ProductField.VitaminB1 -> stringResource(Res.string.vitamin_b1)
        ProductField.VitaminB2 -> stringResource(Res.string.vitamin_b2)
        ProductField.VitaminB3 -> stringResource(Res.string.vitamin_b3)
        ProductField.VitaminB5 -> stringResource(Res.string.vitamin_b5)
        ProductField.VitaminB6 -> stringResource(Res.string.vitamin_b6)
        ProductField.VitaminB7 -> stringResource(Res.string.vitamin_b7)
        ProductField.VitaminB9 -> stringResource(Res.string.vitamin_b9)
        ProductField.VitaminB12 -> stringResource(Res.string.vitamin_b12)
        ProductField.VitaminC -> stringResource(Res.string.vitamin_c)
        ProductField.VitaminD -> stringResource(Res.string.vitamin_d)
        ProductField.VitaminE -> stringResource(Res.string.vitamin_e)
        ProductField.VitaminK -> stringResource(Res.string.vitamin_k)
        ProductField.Manganese -> stringResource(Res.string.mineral_manganese)
        ProductField.Magnesium -> stringResource(Res.string.mineral_magnesium)
        ProductField.Potassium -> stringResource(Res.string.mineral_potassium)
        ProductField.Calcium -> stringResource(Res.string.mineral_calcium)
        ProductField.Copper -> stringResource(Res.string.mineral_copper)
        ProductField.Zinc -> stringResource(Res.string.mineral_zinc)
        ProductField.Sodium -> stringResource(Res.string.mineral_sodium)
        ProductField.Iron -> stringResource(Res.string.mineral_iron)
        ProductField.Phosphorus -> stringResource(Res.string.mineral_phosphorus)
        ProductField.Selenium -> stringResource(Res.string.mineral_selenium)
        ProductField.Iodine -> stringResource(Res.string.mineral_iodine)
        ProductField.Chromium -> stringResource(Res.string.mineral_chromium)
    }
