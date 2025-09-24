package com.maksimowiczm.foodyou.app.ui.goals.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.doubleParser
import com.maksimowiczm.foodyou.app.ui.common.form.nonNegativeDoubleValidator
import com.maksimowiczm.foodyou.app.ui.common.form.rememberFormField
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.common.compose.utility.formatClipZeros
import com.maksimowiczm.foodyou.common.domain.food.NutritionFactsField
import com.maksimowiczm.foodyou.goals.domain.entity.DailyGoal
import com.maksimowiczm.foodyou.settings.domain.entity.NutrientsOrder
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AdditionalGoalsForm(state: AdditionalGoalsFormState, modifier: Modifier = Modifier) {
    val nutrientsOrder = LocalNutrientsOrder.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        nutrientsOrder.forEach {
            when (it) {
                NutrientsOrder.Proteins -> Unit
                NutrientsOrder.Fats -> Fats(state)
                NutrientsOrder.Carbohydrates -> Carbohydrates(state)
                NutrientsOrder.Other -> Other(state)
                NutrientsOrder.Vitamins -> Vitamins(state)
                NutrientsOrder.Minerals -> Minerals(state)
            }
        }
    }
}

@Composable
private fun ColumnScope.Fats(state: AdditionalGoalsFormState) {
    Text(
        text = stringResource(Res.string.nutriment_fats),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
    )
    state.saturatedFats.TextField(
        label = stringResource(Res.string.nutriment_saturated_fats),
        modifier = Modifier.fillMaxWidth(),
    )
    state.transFats.TextField(
        label = stringResource(Res.string.nutriment_trans_fats),
        modifier = Modifier.fillMaxWidth(),
    )
    state.monounsaturatedFats.TextField(
        label = stringResource(Res.string.nutriment_monounsaturated_fats),
        modifier = Modifier.fillMaxWidth(),
    )
    state.polyunsaturatedFats.TextField(
        label = stringResource(Res.string.nutriment_polyunsaturated_fats),
        modifier = Modifier.fillMaxWidth(),
    )
    state.omega3.TextField(
        label = stringResource(Res.string.nutriment_omega_3),
        modifier = Modifier.fillMaxWidth(),
    )
    state.omega6.TextField(
        label = stringResource(Res.string.nutriment_omega_6),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ColumnScope.Carbohydrates(state: AdditionalGoalsFormState) {
    Text(
        text = stringResource(Res.string.nutriment_carbohydrates),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
    )

    state.sugars.TextField(
        label = stringResource(Res.string.nutriment_sugars),
        modifier = Modifier.fillMaxWidth(),
    )
    state.addedSugars.TextField(
        label = stringResource(Res.string.nutriment_added_sugars),
        modifier = Modifier.fillMaxWidth(),
    )
    state.dietaryFiber.TextField(
        label = stringResource(Res.string.nutriment_fiber),
        modifier = Modifier.fillMaxWidth(),
    )
    state.solubleFiber.TextField(
        label = stringResource(Res.string.nutriment_soluble_fiber),
        modifier = Modifier.fillMaxWidth(),
    )
    state.insolubleFiber.TextField(
        label = stringResource(Res.string.nutriment_insoluble_fiber),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ColumnScope.Other(state: AdditionalGoalsFormState) {
    Text(
        text = stringResource(Res.string.headline_other),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
    )
    state.salt.TextField(
        label = stringResource(Res.string.nutriment_salt),
        modifier = Modifier.fillMaxWidth(),
    )
    state.cholesterolMilli.TextField(
        label = stringResource(Res.string.nutriment_cholesterol),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.caffeineMilli.TextField(
        label = stringResource(Res.string.nutriment_caffeine),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
}

@Composable
private fun ColumnScope.Vitamins(state: AdditionalGoalsFormState) {
    Text(
        text = stringResource(Res.string.headline_vitamins),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
    )
    state.vitaminAMicro.TextField(
        label = stringResource(Res.string.vitamin_a),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
    state.vitaminB1Milli.TextField(
        label = stringResource(Res.string.vitamin_b1),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.vitaminB2Milli.TextField(
        label = stringResource(Res.string.vitamin_b2),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.vitaminB3Milli.TextField(
        label = stringResource(Res.string.vitamin_b3),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.vitaminB5Milli.TextField(
        label = stringResource(Res.string.vitamin_b5),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.vitaminB6Milli.TextField(
        label = stringResource(Res.string.vitamin_b6),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.vitaminB7Micro.TextField(
        label = stringResource(Res.string.vitamin_b7),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
    state.vitaminB9Micro.TextField(
        label = stringResource(Res.string.vitamin_b9),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
    state.vitaminB12Micro.TextField(
        label = stringResource(Res.string.vitamin_b12),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
    state.vitaminCMilli.TextField(
        label = stringResource(Res.string.vitamin_c),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.vitaminDMicro.TextField(
        label = stringResource(Res.string.vitamin_d),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
    state.vitaminEMilli.TextField(
        label = stringResource(Res.string.vitamin_e),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.vitaminKMicro.TextField(
        label = stringResource(Res.string.vitamin_k),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
}

@Composable
private fun ColumnScope.Minerals(state: AdditionalGoalsFormState) {
    Text(
        text = stringResource(Res.string.headline_minerals),
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
    )
    state.manganeseMilli.TextField(
        label = stringResource(Res.string.mineral_manganese),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.magnesiumMilli.TextField(
        label = stringResource(Res.string.mineral_magnesium),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.potassiumMilli.TextField(
        label = stringResource(Res.string.mineral_potassium),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.calciumMilli.TextField(
        label = stringResource(Res.string.mineral_calcium),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.copperMilli.TextField(
        label = stringResource(Res.string.mineral_copper),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.zincMilli.TextField(
        label = stringResource(Res.string.mineral_zinc),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.sodiumMilli.TextField(
        label = stringResource(Res.string.mineral_sodium),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.ironMilli.TextField(
        label = stringResource(Res.string.mineral_iron),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.phosphorusMilli.TextField(
        label = stringResource(Res.string.mineral_phosphorus),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short),
    )
    state.seleniumMicro.TextField(
        label = stringResource(Res.string.mineral_selenium),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
    state.iodineMicro.TextField(
        label = stringResource(Res.string.mineral_iodine),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
    state.chromiumMicro.TextField(
        label = stringResource(Res.string.mineral_chromium),
        modifier = Modifier.fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
    )
}

@Composable
private fun FormField<Double, DailyGoalsFormError>.TextField(
    label: String,
    modifier: Modifier = Modifier,
    suffix: String = stringResource(Res.string.unit_gram_short),
    imeAction: ImeAction = ImeAction.Next,
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        label = { Text(label) },
        suffix = { Text(suffix) },
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction),
        isError = error != null,
    )
}

@Composable
internal fun rememberAdditionalGoalsFormState(dailyGoal: DailyGoal): AdditionalGoalsFormState {
    val saturatedFats =
        rememberFormField(initialValue = dailyGoal[NutritionFactsField.SaturatedFats])
    val transFats = rememberFormField(dailyGoal[NutritionFactsField.TransFats])
    val monounsaturatedFats = rememberFormField(dailyGoal[NutritionFactsField.MonounsaturatedFats])
    val polyunsaturatedFats = rememberFormField(dailyGoal[NutritionFactsField.PolyunsaturatedFats])
    val omega3 = rememberFormField(dailyGoal[NutritionFactsField.Omega3])
    val omega6 = rememberFormField(dailyGoal[NutritionFactsField.Omega6])

    val sugars = rememberFormField(initialValue = dailyGoal[NutritionFactsField.Sugars])
    val addedSugars = rememberFormField(dailyGoal[NutritionFactsField.AddedSugars])
    val dietaryFiber = rememberFormField(dailyGoal[NutritionFactsField.DietaryFiber])
    val solubleFiber = rememberFormField(dailyGoal[NutritionFactsField.SolubleFiber])
    val insolubleFiber = rememberFormField(dailyGoal[NutritionFactsField.InsolubleFiber])
    val salt = rememberFormField(dailyGoal[NutritionFactsField.Salt])
    val cholesterolMilli = rememberFormField(dailyGoal[NutritionFactsField.Cholesterol] * 1000)
    val caffeineMilli = rememberFormField(dailyGoal[NutritionFactsField.Caffeine] * 1000)

    val vitaminAMicro = rememberFormField(dailyGoal[NutritionFactsField.VitaminA] * 1000_000)
    val vitaminB1Milli = rememberFormField(dailyGoal[NutritionFactsField.VitaminB1] * 1000)
    val vitaminB2Milli = rememberFormField(dailyGoal[NutritionFactsField.VitaminB2] * 1000)
    val vitaminB3Milli = rememberFormField(dailyGoal[NutritionFactsField.VitaminB3] * 1000)
    val vitaminB5Milli = rememberFormField(dailyGoal[NutritionFactsField.VitaminB5] * 1000)
    val vitaminB6Milli = rememberFormField(dailyGoal[NutritionFactsField.VitaminB6] * 1000)
    val vitaminB7Micro = rememberFormField(dailyGoal[NutritionFactsField.VitaminB7] * 1000_000)
    val vitaminB9Micro = rememberFormField(dailyGoal[NutritionFactsField.VitaminB9] * 1000_000)
    val vitaminB12Micro = rememberFormField(dailyGoal[NutritionFactsField.VitaminB12] * 1000_000)
    val vitaminCMilli = rememberFormField(dailyGoal[NutritionFactsField.VitaminC] * 1000)
    val vitaminDMicro = rememberFormField(dailyGoal[NutritionFactsField.VitaminD] * 1000_000)
    val vitaminEMilli = rememberFormField(dailyGoal[NutritionFactsField.VitaminE] * 1000)
    val vitaminKMicro = rememberFormField(dailyGoal[NutritionFactsField.VitaminK] * 1000_000)

    val manganeseMilli = rememberFormField(dailyGoal[NutritionFactsField.Manganese] * 1000)
    val magnesiumMilli = rememberFormField(dailyGoal[NutritionFactsField.Magnesium] * 1000)
    val potassiumMilli = rememberFormField(dailyGoal[NutritionFactsField.Potassium] * 1000)
    val calciumMilli = rememberFormField(dailyGoal[NutritionFactsField.Calcium] * 1000)
    val copperMilli = rememberFormField(dailyGoal[NutritionFactsField.Copper] * 1000)
    val zincMilli = rememberFormField(dailyGoal[NutritionFactsField.Zinc] * 1000)
    val sodiumMilli = rememberFormField(dailyGoal[NutritionFactsField.Sodium] * 1000)
    val ironMilli = rememberFormField(dailyGoal[NutritionFactsField.Iron] * 1000)
    val phosphorusMilli = rememberFormField(dailyGoal[NutritionFactsField.Phosphorus] * 1000)
    val seleniumMicro = rememberFormField(dailyGoal[NutritionFactsField.Selenium] * 1000_000)
    val iodineMicro = rememberFormField(dailyGoal[NutritionFactsField.Iodine] * 1000_000)
    val chromiumMicro = rememberFormField(dailyGoal[NutritionFactsField.Chromium] * 1000_000)

    val isModified = remember {
        derivedStateOf {
            saturatedFats.value != dailyGoal[NutritionFactsField.SaturatedFats] ||
                transFats.value != dailyGoal[NutritionFactsField.TransFats] ||
                monounsaturatedFats.value != dailyGoal[NutritionFactsField.MonounsaturatedFats] ||
                polyunsaturatedFats.value != dailyGoal[NutritionFactsField.PolyunsaturatedFats] ||
                omega3.value != dailyGoal[NutritionFactsField.Omega3] ||
                omega6.value != dailyGoal[NutritionFactsField.Omega6] ||
                sugars.value != dailyGoal[NutritionFactsField.Sugars] ||
                addedSugars.value != dailyGoal[NutritionFactsField.AddedSugars] ||
                dietaryFiber.value != dailyGoal[NutritionFactsField.DietaryFiber] ||
                solubleFiber.value != dailyGoal[NutritionFactsField.SolubleFiber] ||
                insolubleFiber.value != dailyGoal[NutritionFactsField.InsolubleFiber] ||
                salt.value != dailyGoal[NutritionFactsField.Salt] ||
                cholesterolMilli.value != (dailyGoal[NutritionFactsField.Cholesterol] * 1000) ||
                caffeineMilli.value != (dailyGoal[NutritionFactsField.Caffeine] * 1000) ||
                vitaminAMicro.value != dailyGoal[NutritionFactsField.VitaminA] * 1000_000 ||
                vitaminB1Milli.value != dailyGoal[NutritionFactsField.VitaminB1] * 1000 ||
                vitaminB2Milli.value != (dailyGoal[NutritionFactsField.VitaminB2] * 1000) ||
                vitaminB3Milli.value != (dailyGoal[NutritionFactsField.VitaminB3] * 1000) ||
                vitaminB5Milli.value != (dailyGoal[NutritionFactsField.VitaminB5] * 1000) ||
                vitaminB6Milli.value != (dailyGoal[NutritionFactsField.VitaminB6] * 1000) ||
                vitaminB7Micro.value != (dailyGoal[NutritionFactsField.VitaminB7] * 1000_000) ||
                vitaminB9Micro.value != (dailyGoal[NutritionFactsField.VitaminB9] * 1000_000) ||
                vitaminB12Micro.value != (dailyGoal[NutritionFactsField.VitaminB12] * 1000_000) ||
                vitaminCMilli.value != (dailyGoal[NutritionFactsField.VitaminC] * 1000) ||
                vitaminDMicro.value != (dailyGoal[NutritionFactsField.VitaminD] * 1000_000) ||
                vitaminEMilli.value != (dailyGoal[NutritionFactsField.VitaminE] * 1000) ||
                vitaminKMicro.value != (dailyGoal[NutritionFactsField.VitaminK] * 1000_000) ||
                manganeseMilli.value != (dailyGoal[NutritionFactsField.Manganese] * 1000) ||
                magnesiumMilli.value != (dailyGoal[NutritionFactsField.Magnesium] * 1000) ||
                potassiumMilli.value != (dailyGoal[NutritionFactsField.Potassium] * 1000) ||
                calciumMilli.value != (dailyGoal[NutritionFactsField.Calcium] * 1000) ||
                copperMilli.value != (dailyGoal[NutritionFactsField.Copper] * 1000) ||
                zincMilli.value != (dailyGoal[NutritionFactsField.Zinc] * 1000) ||
                sodiumMilli.value != (dailyGoal[NutritionFactsField.Sodium] * 1000) ||
                ironMilli.value != (dailyGoal[NutritionFactsField.Iron] * 1000) ||
                phosphorusMilli.value != (dailyGoal[NutritionFactsField.Phosphorus] * 1000) ||
                seleniumMicro.value != (dailyGoal[NutritionFactsField.Selenium] * 1000_000) ||
                iodineMicro.value != (dailyGoal[NutritionFactsField.Iodine] * 1000_000) ||
                chromiumMicro.value != (dailyGoal[NutritionFactsField.Chromium] * 1000_000)
        }
    }

    return remember(
        saturatedFats,
        transFats,
        monounsaturatedFats,
        polyunsaturatedFats,
        omega3,
        omega6,
        sugars,
        addedSugars,
        dietaryFiber,
        solubleFiber,
        insolubleFiber,
        salt,
        cholesterolMilli,
        caffeineMilli,
        vitaminAMicro,
        vitaminB1Milli,
        vitaminB2Milli,
        vitaminB3Milli,
        vitaminB5Milli,
        vitaminB6Milli,
        vitaminB7Micro,
        vitaminB9Micro,
        vitaminB12Micro,
        vitaminCMilli,
        vitaminDMicro,
        vitaminEMilli,
        vitaminKMicro,
        manganeseMilli,
        magnesiumMilli,
        potassiumMilli,
        calciumMilli,
        copperMilli,
        zincMilli,
        sodiumMilli,
        ironMilli,
        phosphorusMilli,
        seleniumMicro,
        iodineMicro,
        chromiumMicro,
        isModified,
    ) {
        AdditionalGoalsFormState(
            saturatedFats = saturatedFats,
            transFats = transFats,
            monounsaturatedFats = monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats,
            omega3 = omega3,
            omega6 = omega6,
            sugars = sugars,
            addedSugars = addedSugars,
            dietaryFiber = dietaryFiber,
            solubleFiber = solubleFiber,
            insolubleFiber = insolubleFiber,
            salt = salt,
            cholesterolMilli = cholesterolMilli,
            caffeineMilli = caffeineMilli,
            vitaminAMicro = vitaminAMicro,
            vitaminB1Milli = vitaminB1Milli,
            vitaminB2Milli = vitaminB2Milli,
            vitaminB3Milli = vitaminB3Milli,
            vitaminB5Milli = vitaminB5Milli,
            vitaminB6Milli = vitaminB6Milli,
            vitaminB7Micro = vitaminB7Micro,
            vitaminB9Micro = vitaminB9Micro,
            vitaminB12Micro = vitaminB12Micro,
            vitaminCMilli = vitaminCMilli,
            vitaminDMicro = vitaminDMicro,
            vitaminEMilli = vitaminEMilli,
            vitaminKMicro = vitaminKMicro,
            manganeseMilli = manganeseMilli,
            magnesiumMilli = magnesiumMilli,
            potassiumMilli = potassiumMilli,
            calciumMilli = calciumMilli,
            copperMilli = copperMilli,
            zincMilli = zincMilli,
            sodiumMilli = sodiumMilli,
            ironMilli = ironMilli,
            phosphorusMilli = phosphorusMilli,
            seleniumMicro = seleniumMicro,
            iodineMicro = iodineMicro,
            chromiumMicro = chromiumMicro,
            isModifiedState = isModified,
        )
    }
}

@Composable
private fun rememberFormField(initialValue: Double): FormField<Double, DailyGoalsFormError> =
    rememberFormField(
        initialValue = initialValue,
        parser =
            doubleParser(
                onBlank = { DailyGoalsFormError.Required },
                onNotANumber = { DailyGoalsFormError.NotANumber },
            ),
        validator = nonNegativeDoubleValidator(onNegative = { DailyGoalsFormError.Negative }),
        textFieldState = rememberTextFieldState(initialValue.formatClipZeros()),
    )

@Stable
internal class AdditionalGoalsFormState(
    val saturatedFats: FormField<Double, DailyGoalsFormError>,
    val transFats: FormField<Double, DailyGoalsFormError>,
    val monounsaturatedFats: FormField<Double, DailyGoalsFormError>,
    val polyunsaturatedFats: FormField<Double, DailyGoalsFormError>,
    val omega3: FormField<Double, DailyGoalsFormError>,
    val omega6: FormField<Double, DailyGoalsFormError>,
    val sugars: FormField<Double, DailyGoalsFormError>,
    val addedSugars: FormField<Double, DailyGoalsFormError>,
    val dietaryFiber: FormField<Double, DailyGoalsFormError>,
    val solubleFiber: FormField<Double, DailyGoalsFormError>,
    val insolubleFiber: FormField<Double, DailyGoalsFormError>,
    val salt: FormField<Double, DailyGoalsFormError>,
    val cholesterolMilli: FormField<Double, DailyGoalsFormError>,
    val caffeineMilli: FormField<Double, DailyGoalsFormError>,
    val vitaminAMicro: FormField<Double, DailyGoalsFormError>,
    val vitaminB1Milli: FormField<Double, DailyGoalsFormError>,
    val vitaminB2Milli: FormField<Double, DailyGoalsFormError>,
    val vitaminB3Milli: FormField<Double, DailyGoalsFormError>,
    val vitaminB5Milli: FormField<Double, DailyGoalsFormError>,
    val vitaminB6Milli: FormField<Double, DailyGoalsFormError>,
    val vitaminB7Micro: FormField<Double, DailyGoalsFormError>,
    val vitaminB9Micro: FormField<Double, DailyGoalsFormError>,
    val vitaminB12Micro: FormField<Double, DailyGoalsFormError>,
    val vitaminCMilli: FormField<Double, DailyGoalsFormError>,
    val vitaminDMicro: FormField<Double, DailyGoalsFormError>,
    val vitaminEMilli: FormField<Double, DailyGoalsFormError>,
    val vitaminKMicro: FormField<Double, DailyGoalsFormError>,
    val manganeseMilli: FormField<Double, DailyGoalsFormError>,
    val magnesiumMilli: FormField<Double, DailyGoalsFormError>,
    val potassiumMilli: FormField<Double, DailyGoalsFormError>,
    val calciumMilli: FormField<Double, DailyGoalsFormError>,
    val copperMilli: FormField<Double, DailyGoalsFormError>,
    val zincMilli: FormField<Double, DailyGoalsFormError>,
    val sodiumMilli: FormField<Double, DailyGoalsFormError>,
    val ironMilli: FormField<Double, DailyGoalsFormError>,
    val phosphorusMilli: FormField<Double, DailyGoalsFormError>,
    val seleniumMicro: FormField<Double, DailyGoalsFormError>,
    val iodineMicro: FormField<Double, DailyGoalsFormError>,
    val chromiumMicro: FormField<Double, DailyGoalsFormError>,
    isModifiedState: State<Boolean>,
) {
    val isValid by derivedStateOf {
        listOf(
                saturatedFats,
                transFats,
                monounsaturatedFats,
                polyunsaturatedFats,
                omega3,
                omega6,
                sugars,
                addedSugars,
                dietaryFiber,
                solubleFiber,
                insolubleFiber,
                salt,
                cholesterolMilli,
                caffeineMilli,
                vitaminAMicro,
                vitaminB1Milli,
                vitaminB2Milli,
                vitaminB3Milli,
                vitaminB5Milli,
                vitaminB6Milli,
                vitaminB7Micro,
                vitaminB9Micro,
                vitaminB12Micro,
                vitaminCMilli,
                vitaminDMicro,
                vitaminEMilli,
                vitaminKMicro,
                manganeseMilli,
                magnesiumMilli,
                potassiumMilli,
                calciumMilli,
                copperMilli,
                zincMilli,
                sodiumMilli,
                ironMilli,
                phosphorusMilli,
                seleniumMicro,
                iodineMicro,
                chromiumMicro,
            )
            .all { it.error == null }
    }

    val isModified: Boolean by isModifiedState
}
