package com.maksimowiczm.foodyou.app.ui.food.diary.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.shared.component.FoodErrorListItem
import com.maksimowiczm.foodyou.app.ui.shared.component.FoodListItem
import com.maksimowiczm.foodyou.app.ui.shared.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.shared.utility.stringResourceWithWeight
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodRecipe
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodRecipeIngredient
import com.maksimowiczm.foodyou.shared.compose.utility.formatClipZeros
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun Ingredients(
    ingredients: List<DiaryFoodRecipeIngredient>,
    modifier: Modifier = Modifier,
) {
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
            val proteins = facts.proteins.value
            val carbs = facts.carbohydrates.value
            val fats = facts.fats.value
            val energy = facts.energy.value
            val measurementString =
                ingredient.measurement.stringResourceWithWeight(
                    totalWeight = ingredient.food.totalWeight,
                    servingWeight = ingredient.food.servingWeight,
                    isLiquid = ingredient.food.isLiquid,
                )

            if (
                proteins == null ||
                    carbs == null ||
                    fats == null ||
                    energy == null ||
                    measurementString == null
            ) {
                FoodErrorListItem(
                    headline = ingredient.food.name,
                    errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
                    contentPadding = contentPadding,
                )
            } else {
                FoodListItem(
                    name = { Text(ingredient.food.name) },
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
                        Text(LocalEnergyFormatter.current.formatEnergy(energy.roundToInt()))
                    },
                    measurement = { Text(measurementString) },
                    contentPadding = contentPadding,
                    isRecipe = ingredient.food is DiaryFoodRecipe,
                )
            }
        }
    }
}
