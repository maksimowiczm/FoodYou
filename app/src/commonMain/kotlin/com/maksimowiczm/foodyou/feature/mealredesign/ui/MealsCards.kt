package com.maksimowiczm.foodyou.feature.mealredesign.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.domain.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.domain.model.NutritionFacts
import com.maksimowiczm.foodyou.core.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.mealredesign.domain.Meal
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MealsCards(
    homeState: HomeState,
    onAdd: (epochDay: Int, mealId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsCardsViewModel = koinViewModel()
) {
    val meals =
        viewModel.observeMeals(homeState.selectedDate).collectAsStateWithLifecycle(null).value

    if (meals == null) {
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        meals.forEach { meal ->
            MealCard(
                meal = meal,
                onAddFood = {
                    onAdd(homeState.selectedDate.toEpochDays(), meal.id)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MealCard(meal: Meal, onAddFood: () -> Unit, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val dateFormatter = LocalDateFormatter.current
    val enDash = stringResource(Res.string.en_dash)
    val allDayString = stringResource(Res.string.headline_all_day)

    val timeString = remember(dateFormatter, meal, enDash, allDayString) {
        if (meal.isAllDay) {
            allDayString
        } else {
            buildString {
                append(dateFormatter.formatTime(meal.from))
                append(" $enDash ")
                append(dateFormatter.formatTime(meal.to))
            }
        }
    }

    FoodYouHomeCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.headlineMediumEmphasized
                )
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (meal.food.isNotEmpty()) {
                FoodContainer(
                    food = meal.food,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ValueColumn(
                    label = stringResource(Res.string.unit_kcal),
                    value = meal.calories,
                    color = MaterialTheme.colorScheme.onSurface
                )

                ValueColumn(
                    label = stringResource(Res.string.nutriment_proteins_short),
                    value = meal.proteins,
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                )

                ValueColumn(
                    label = stringResource(Res.string.nutriment_carbohydrates_short),
                    value = meal.carbohydrates,
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                )

                ValueColumn(
                    label = stringResource(Res.string.nutriment_fats_short),
                    value = meal.fats,
                    color = nutrientsPalette.fatsOnSurfaceContainer
                )

                Spacer(Modifier.weight(1f))

                FilledIconButton(
                    onClick = onAddFood,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.action_add)
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodContainer(food: List<FoodWithMeasurement>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            food.forEach { foodWithMeasurement ->
                FoodListItem(
                    foodWithMeasurement = foodWithMeasurement,
                    onMore = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ValueColumn(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides color,
            LocalTextStyle provides MaterialTheme.typography.labelMedium
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = if (value == 0) {
                    stringResource(Res.string.em_dash)
                } else {
                    value.toString()
                }
            )
        }
    }
}

fun testNutritionFacts(
    proteins: Float = 10f,
    carbohydrates: Float = 15f,
    fats: Float = 5f,
    calories: Float = 145f,
    saturatedFats: Float? = 2f,
    monounsaturatedFats: Float? = 1f,
    polyunsaturatedFats: Float? = 1f,
    omega3: Float? = 0.5f,
    omega6: Float? = 0.5f,
    sugars: Float? = 5f,
    salt: Float? = 0.5f,
    fiber: Float? = 3f,
    cholesterolMilli: Float? = 0.1f,
    caffeineMilli: Float? = 0.1f,
    vitaminAMicro: Float? = 0.1f,
    vitaminB1Milli: Float? = 0.1f,
    vitaminB2Milli: Float? = 0.1f,
    vitaminB3Milli: Float? = 0.1f,
    vitaminB5Milli: Float? = 0.1f,
    vitaminB6Milli: Float? = 0.1f,
    vitaminB7Micro: Float? = 0.1f,
    vitaminB9Micro: Float? = 0.1f,
    vitaminB12Micro: Float? = 0.1f,
    vitaminCMilli: Float? = 0.1f,
    vitaminDMicro: Float? = 0.1f,
    vitaminEMilli: Float? = 0.1f,
    vitaminKMicro: Float? = 0.1f,
    manganeseMilli: Float? = 0.1f,
    magnesiumMilli: Float? = 0.1f,
    potassiumMilli: Float? = 0.1f,
    calciumMilli: Float? = 0.1f,
    copperMilli: Float? = 0.1f,
    zincMilli: Float? = 0.1f,
    sodiumMilli: Float? = 0.1f,
    ironMilli: Float? = 0.1f,
    phosphorusMilli: Float? = 0.1f,
    seleniumMicro: Float? = 0.1f,
    iodineMicro: Float? = 0.1f
) = NutritionFacts(
    proteins = proteins.toNutrientValue(),
    carbohydrates = carbohydrates.toNutrientValue(),
    fats = fats.toNutrientValue(),
    calories = calories.toNutrientValue(),
    saturatedFats = saturatedFats.toNutrientValue(),
    monounsaturatedFats = monounsaturatedFats.toNutrientValue(),
    polyunsaturatedFats = polyunsaturatedFats.toNutrientValue(),
    omega3 = omega3.toNutrientValue(),
    omega6 = omega6.toNutrientValue(),
    sugars = sugars.toNutrientValue(),
    salt = salt.toNutrientValue(),
    fiber = fiber.toNutrientValue(),
    cholesterolMilli = cholesterolMilli.toNutrientValue(),
    caffeineMilli = caffeineMilli.toNutrientValue(),
    vitaminAMicro = vitaminAMicro.toNutrientValue(),
    vitaminB1Milli = vitaminB1Milli.toNutrientValue(),
    vitaminB2Milli = vitaminB2Milli.toNutrientValue(),
    vitaminB3Milli = vitaminB3Milli.toNutrientValue(),
    vitaminB5Milli = vitaminB5Milli.toNutrientValue(),
    vitaminB6Milli = vitaminB6Milli.toNutrientValue(),
    vitaminB7Micro = vitaminB7Micro.toNutrientValue(),
    vitaminB9Micro = vitaminB9Micro.toNutrientValue(),
    vitaminB12Micro = vitaminB12Micro.toNutrientValue(),
    vitaminCMilli = vitaminCMilli.toNutrientValue(),
    vitaminDMicro = vitaminDMicro.toNutrientValue(),
    vitaminEMilli = vitaminEMilli.toNutrientValue(),
    vitaminKMicro = vitaminKMicro.toNutrientValue(),
    manganeseMilli = manganeseMilli.toNutrientValue(),
    magnesiumMilli = magnesiumMilli.toNutrientValue(),
    potassiumMilli = potassiumMilli.toNutrientValue(),
    calciumMilli = calciumMilli.toNutrientValue(),
    copperMilli = copperMilli.toNutrientValue(),
    zincMilli = zincMilli.toNutrientValue(),
    sodiumMilli = sodiumMilli.toNutrientValue(),
    ironMilli = ironMilli.toNutrientValue(),
    phosphorusMilli = phosphorusMilli.toNutrientValue(),
    seleniumMicro = seleniumMicro.toNutrientValue(),
    iodineMicro = iodineMicro.toNutrientValue()
)
