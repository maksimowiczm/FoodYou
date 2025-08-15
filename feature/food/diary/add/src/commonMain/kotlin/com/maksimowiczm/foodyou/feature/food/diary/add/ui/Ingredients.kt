package com.maksimowiczm.foodyou.feature.food.diary.add.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.IngredientModel
import com.maksimowiczm.foodyou.feature.shared.ui.FoodErrorListItem
import com.maksimowiczm.foodyou.feature.shared.ui.FoodListItem
import com.maksimowiczm.foodyou.feature.shared.ui.stringResourceWithWeight
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.shared.ui.res.stringResource
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.error_food_is_missing_required_fields
import foodyou.app.generated.resources.headline_ingredients
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun Ingredients(ingredients: List<IngredientModel>, modifier: Modifier = Modifier) {
    val g = stringResource(Res.string.unit_gram_short)
    val kcal = stringResource(Res.string.unit_kcal)

    val contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)

    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_ingredients),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        ingredients.forEach { ingredient ->
            val facts = ingredient.nutritionFacts
            val proteins = facts?.proteins?.value
            val carbs = facts?.carbohydrates?.value
            val fats = facts?.fats?.value
            val energy = facts?.energy?.value
            val measurementString =
                ingredient.measurement.stringResourceWithWeight(
                    totalWeight = ingredient.totalWeight,
                    servingWeight = ingredient.servingWeight,
                    isLiquid = ingredient.isLiquid,
                )

            if (
                proteins == null ||
                    carbs == null ||
                    fats == null ||
                    energy == null ||
                    measurementString == null
            ) {
                FoodErrorListItem(
                    headline = ingredient.name,
                    errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
                    contentPadding = contentPadding,
                )
            } else {
                FoodListItem(
                    name = { Text(ingredient.name) },
                    proteins = {
                        val text = proteins.formatClipZeros() + " $g"
                        Text(text)
                    },
                    carbohydrates = {
                        val text = carbs.formatClipZeros() + " $g"
                        Text(text)
                    },
                    fats = {
                        val text = fats.formatClipZeros() + " $g"
                        Text(text)
                    },
                    calories = {
                        val text = energy.roundToInt().toString() + " $kcal"
                        Text(text)
                    },
                    measurement = { Text(measurementString) },
                    contentPadding = contentPadding,
                    isRecipe = ingredient.isRecipe,
                )
            }
        }
    }
}
