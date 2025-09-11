package com.maksimowiczm.foodyou.feature.food.diary.add.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.shared.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.FoodModel
import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.RecipeModel
import com.maksimowiczm.foodyou.feature.food.shared.ui.EnergyProgressIndicator
import com.maksimowiczm.foodyou.feature.food.shared.ui.NutrientList
import com.maksimowiczm.foodyou.feature.shared.ui.stringResourceWithWeight
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.domain.food.isComplete
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement

@Composable
internal fun NutrientList(
    food: FoodModel,
    measurement: Measurement,
    onEditFood: (FoodId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val facts =
        remember(food, measurement) {
            val weight =
                food.weight(measurement)
                    ?: error("Invalid measurement: $measurement for food: ${food.foodId}")
            food.nutritionFacts * (weight / 100)
        }

    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ViewList, contentDescription = null)
            }

            val proteins = facts.proteins.value
            val carbohydrates = facts.carbohydrates.value
            val fats = facts.fats.value

            if (proteins != null && carbohydrates != null && fats != null) {
                EnergyProgressIndicator(
                    proteins = proteins.toFloat(),
                    carbohydrates = carbohydrates.toFloat(),
                    fats = fats.toFloat(),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        val measurementString =
            measurement.stringResourceWithWeight(
                totalWeight = food.totalWeight,
                servingWeight = food.servingWeight,
                isLiquid = food.isLiquid,
            ) ?: error("Invalid measurement: $measurement for ${food.foodId}")

        Text(
            text = measurementString,
            modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp),
            style = MaterialTheme.typography.labelLarge,
        )

        NutrientList(facts)

        if (food is RecipeModel && !food.nutritionFacts.isComplete) {
            val foods =
                food.allIngredients
                    .filter { (foodId, _, facts) -> foodId is FoodId.Product && !facts.isComplete }
                    .map { (foodId, name) -> foodId to name }

            IncompleteFoodsList(
                foods = foods.map { (_, name) -> name }.distinct(),
                onFoodClick = { i -> onEditFood(foods[i].first) },
                modifier = Modifier.padding(8.dp),
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}
