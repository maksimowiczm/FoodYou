package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycleInitialBlock
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField
import com.maksimowiczm.foodyou.feature.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.GoalsPreference
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DailyGoalsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val goalsPreference = userPreference<GoalsPreference>()
    val weeklyGoals by goalsPreference.collectAsStateWithLifecycleInitialBlock()

    val goal = weeklyGoals.monday

    val proteins = goal[NutritionFactsField.Proteins].toFloat()
    val carbohydrates = goal[NutritionFactsField.Carbohydrates].toFloat()
    val fats = goal[NutritionFactsField.Fats].toFloat()
    val energy = goal[NutritionFactsField.Energy].roundToInt()

    val sliderState = rememberMacroInputSliderFormState(
        proteins = NutrientsHelper.proteinsPercentage(energy, proteins) * 100f,
        carbohydrates = NutrientsHelper.carbohydratesPercentage(
            energy,
            carbohydrates
        ) * 100f,
        fats = NutrientsHelper.fatsPercentage(energy, fats) * 100f,
        energy = energy
    )
    val weightState = rememberMacroWeightInputFormState(
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        energy = energy
    )

    val additionalState = rememberAdditionalGoalsFormState(goal)

    var useDistribution by rememberSaveable { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_daily_goals)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    FilledIconButton(
                        onClick = {
                            // TODO
                        },
                        enabled = if (useDistribution) {
                            additionalState.isValid
                        } else {
                            weightState.isValid && additionalState.isValid
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp)
        ) {
            item {
                DailyGoalsForm(
                    useDistribution = useDistribution,
                    onUseDistributionChange = { useDistribution = it },
                    sliderState = sliderState,
                    weightState = weightState,
                    additionalState = additionalState,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DailyGoalsForm(
    useDistribution: Boolean,
    onUseDistributionChange: (Boolean) -> Unit,
    sliderState: MacroInputSliderFormState,
    weightState: MacroWeightInputFormState,
    additionalState: AdditionalGoalsFormState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
//        Text(
//            text = "Pick the days",
//            modifier = Modifier.padding(contentPadding),
//            style = MaterialTheme.typography.labelLarge,
//            color = MaterialTheme.colorScheme.primary
//        )
//        Spacer(Modifier.height(8.dp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable {
//                    // TODO
//                }
//                .padding(contentPadding),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Checkbox(
//                modifier = Modifier.padding(
//                    vertical = 16.dp
//                ),
//                checked = false,
//                onCheckedChange = null
//            )
//            Text(
//                text = "Set separate goals for each day",
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
//        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.action_set_goals),
            modifier = Modifier.padding(contentPadding),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        WeightOrPercentageToggle(
            useDistribution = useDistribution,
            onUseDistributionChange = onUseDistributionChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .padding(vertical = 8.dp)
        )
        if (useDistribution) {
            MacroInputSliderForm(
                state = sliderState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        } else {
            MacroWeightInputForm(
                state = weightState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        AdditionalGoalsForm(
            state = additionalState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WeightOrPercentageToggle(
    useDistribution: Boolean,
    onUseDistributionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            ButtonGroupDefaults.ConnectedSpaceBetween,
            Alignment.CenterHorizontally
        )
    ) {
        ToggleButton(
            checked = !useDistribution,
            onCheckedChange = { onUseDistributionChange(!it) },
            modifier = Modifier
                .height(56.dp)
                .semantics { role = Role.RadioButton }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_weight),
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Weight")
        }
        ToggleButton(
            checked = useDistribution,
            onCheckedChange = { onUseDistributionChange(it) },
            modifier = Modifier
                .height(56.dp)
                .semantics { role = Role.RadioButton }
        ) {
            Icon(
                imageVector = Icons.Outlined.Percent,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Percentage")
        }
    }
}

private fun intoDailyGoals(
    sliderState: MacroInputSliderFormState,
    additionalState: AdditionalGoalsFormState
): DailyGoal {
    val energy = sliderState.energy.value
    val proteins = NutrientsHelper.proteinsPercentageToGrams(energy, sliderState.proteins / 100f)
    val carbohydrates =
        NutrientsHelper.carbohydratesPercentageToGrams(energy, sliderState.carbohydrates / 100f)
    val fats = NutrientsHelper.fatsPercentageToGrams(energy, sliderState.fats / 100f)

    val map = NutritionFactsField.entries.associateWith {
        when (it) {
            NutritionFactsField.Energy -> energy.toDouble()
            NutritionFactsField.Proteins -> proteins.toDouble()
            NutritionFactsField.Fats -> fats.toDouble()
            NutritionFactsField.SaturatedFats -> additionalState.saturatedFats.value
            NutritionFactsField.TransFats -> additionalState.transFats.value
            NutritionFactsField.MonounsaturatedFats -> additionalState.monounsaturatedFats.value
            NutritionFactsField.PolyunsaturatedFats -> additionalState.polyunsaturatedFats.value
            NutritionFactsField.Omega3 -> additionalState.omega3.value
            NutritionFactsField.Omega6 -> additionalState.omega6.value
            NutritionFactsField.Carbohydrates -> carbohydrates.toDouble()
            NutritionFactsField.Sugars -> additionalState.sugars.value
            NutritionFactsField.AddedSugars -> additionalState.addedSugars.value
            NutritionFactsField.DietaryFiber -> additionalState.dietaryFiber.value
            NutritionFactsField.SolubleFiber -> additionalState.solubleFiber.value
            NutritionFactsField.InsolubleFiber -> additionalState.insolubleFiber.value
            NutritionFactsField.Salt -> additionalState.salt.value
            NutritionFactsField.Cholesterol -> additionalState.cholesterolMilli.value / 1000
            NutritionFactsField.Caffeine -> additionalState.caffeineMilli.value / 1000
            NutritionFactsField.VitaminA -> additionalState.vitaminAMicro.value / 1000_000
            NutritionFactsField.VitaminB1 -> additionalState.vitaminB1Milli.value / 1000
            NutritionFactsField.VitaminB2 -> additionalState.vitaminB2Milli.value / 1000
            NutritionFactsField.VitaminB3 -> additionalState.vitaminB3Milli.value / 1000
            NutritionFactsField.VitaminB5 -> additionalState.vitaminB5Milli.value / 1000
            NutritionFactsField.VitaminB6 -> additionalState.vitaminB6Milli.value / 1000
            NutritionFactsField.VitaminB7 -> additionalState.vitaminB7Micro.value / 1000_000
            NutritionFactsField.VitaminB9 -> additionalState.vitaminB9Micro.value / 1000_000
            NutritionFactsField.VitaminB12 -> additionalState.vitaminB12Micro.value / 1000_000
            NutritionFactsField.VitaminC -> additionalState.vitaminCMilli.value / 1000
            NutritionFactsField.VitaminD -> additionalState.vitaminDMicro.value / 1000_000
            NutritionFactsField.VitaminE -> additionalState.vitaminEMilli.value / 1000
            NutritionFactsField.VitaminK -> additionalState.vitaminKMicro.value / 1000_000
            NutritionFactsField.Manganese -> additionalState.manganeseMilli.value / 1000
            NutritionFactsField.Magnesium -> additionalState.magnesiumMilli.value / 1000
            NutritionFactsField.Potassium -> additionalState.potassiumMilli.value / 1000
            NutritionFactsField.Calcium -> additionalState.calciumMilli.value / 1000
            NutritionFactsField.Copper -> additionalState.copperMilli.value / 1000
            NutritionFactsField.Zinc -> additionalState.zincMilli.value / 1000
            NutritionFactsField.Sodium -> additionalState.sodiumMilli.value / 1000
            NutritionFactsField.Iron -> additionalState.ironMilli.value / 1000
            NutritionFactsField.Phosphorus -> additionalState.phosphorusMilli.value / 1000
            NutritionFactsField.Selenium -> additionalState.seleniumMicro.value / 1000_000
            NutritionFactsField.Iodine -> additionalState.iodineMicro.value / 1000_000
            NutritionFactsField.Chromium -> additionalState.chromiumMicro.value / 1000_000
        }
    }

    return DailyGoal(
        map = map,
        isDistribution = true
    )
}

private fun intoDailyGoals(
    weightState: MacroWeightInputFormState,
    additionalState: AdditionalGoalsFormState
): DailyGoal {
    val energy = weightState.energy.value
    val proteins = weightState.proteins.value
    val carbohydrates = weightState.carbohydrates.value
    val fats = weightState.fats.value

    val map = NutritionFactsField.entries.associateWith {
        when (it) {
            NutritionFactsField.Energy -> energy.toDouble()
            NutritionFactsField.Proteins -> proteins.toDouble()
            NutritionFactsField.Fats -> fats.toDouble()
            NutritionFactsField.SaturatedFats -> additionalState.saturatedFats.value
            NutritionFactsField.TransFats -> additionalState.transFats.value
            NutritionFactsField.MonounsaturatedFats -> additionalState.monounsaturatedFats.value
            NutritionFactsField.PolyunsaturatedFats -> additionalState.polyunsaturatedFats.value
            NutritionFactsField.Omega3 -> additionalState.omega3.value
            NutritionFactsField.Omega6 -> additionalState.omega6.value
            NutritionFactsField.Carbohydrates -> carbohydrates.toDouble()
            NutritionFactsField.Sugars -> additionalState.sugars.value
            NutritionFactsField.AddedSugars -> additionalState.addedSugars.value
            NutritionFactsField.DietaryFiber -> additionalState.dietaryFiber.value
            NutritionFactsField.SolubleFiber -> additionalState.solubleFiber.value
            NutritionFactsField.InsolubleFiber -> additionalState.insolubleFiber.value
            NutritionFactsField.Salt -> additionalState.salt.value
            NutritionFactsField.Cholesterol -> additionalState.cholesterolMilli.value / 1000
            NutritionFactsField.Caffeine -> additionalState.caffeineMilli.value / 1000
            NutritionFactsField.VitaminA -> additionalState.vitaminAMicro.value / 1000_000
            NutritionFactsField.VitaminB1 -> additionalState.vitaminB1Milli.value / 1000
            NutritionFactsField.VitaminB2 -> additionalState.vitaminB2Milli.value / 1000
            NutritionFactsField.VitaminB3 -> additionalState.vitaminB3Milli.value / 1000
            NutritionFactsField.VitaminB5 -> additionalState.vitaminB5Milli.value / 1000
            NutritionFactsField.VitaminB6 -> additionalState.vitaminB6Milli.value / 1000
            NutritionFactsField.VitaminB7 -> additionalState.vitaminB7Micro.value / 1000_000
            NutritionFactsField.VitaminB9 -> additionalState.vitaminB9Micro.value / 1000_000
            NutritionFactsField.VitaminB12 -> additionalState.vitaminB12Micro.value / 1000_000
            NutritionFactsField.VitaminC -> additionalState.vitaminCMilli.value / 1000
            NutritionFactsField.VitaminD -> additionalState.vitaminDMicro.value / 1000_000
            NutritionFactsField.VitaminE -> additionalState.vitaminEMilli.value / 1000
            NutritionFactsField.VitaminK -> additionalState.vitaminKMicro.value / 1000_000
            NutritionFactsField.Manganese -> additionalState.manganeseMilli.value / 1000
            NutritionFactsField.Magnesium -> additionalState.magnesiumMilli.value / 1000
            NutritionFactsField.Potassium -> additionalState.potassiumMilli.value / 1000
            NutritionFactsField.Calcium -> additionalState.calciumMilli.value / 1000
            NutritionFactsField.Copper -> additionalState.copperMilli.value / 1000
            NutritionFactsField.Zinc -> additionalState.zincMilli.value / 1000
            NutritionFactsField.Sodium -> additionalState.sodiumMilli.value / 1000
            NutritionFactsField.Iron -> additionalState.ironMilli.value / 1000
            NutritionFactsField.Phosphorus -> additionalState.phosphorusMilli.value / 1000
            NutritionFactsField.Selenium -> additionalState.seleniumMicro.value / 1000_000
            NutritionFactsField.Iodine -> additionalState.iodineMicro.value / 1000_000
            NutritionFactsField.Chromium -> additionalState.chromiumMicro.value / 1000_000
        }
    }

    return DailyGoal(
        map = map,
        isDistribution = false
    )
}
