package com.maksimowiczm.foodyou.app.ui.food.diary.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.FoodErrorListItem
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItem
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.stringResourceWithWeight
import com.maksimowiczm.foodyou.common.compose.extension.add
import com.maksimowiczm.foodyou.common.compose.extension.horizontal
import com.maksimowiczm.foodyou.common.compose.extension.vertical
import com.maksimowiczm.foodyou.common.compose.utility.formatClipZeros
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodRecipe
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodRecipeIngredient
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun Ingredients(
    ingredients: List<DiaryFoodRecipeIngredient>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val g = stringResource(Res.string.unit_gram_short)

    val verticalPadding = contentPadding.vertical()
    val horizontal = contentPadding.horizontal()

    Column(modifier.padding(verticalPadding)) {
        Text(
            text = stringResource(Res.string.headline_ingredients),
            modifier = Modifier.padding(horizontal),
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
                    contentPadding = horizontal.add(vertical = 8.dp),
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
                    contentPadding = horizontal.add(vertical = 8.dp),
                    isRecipe = ingredient.food is DiaryFoodRecipe,
                )
            }
        }
    }
}
