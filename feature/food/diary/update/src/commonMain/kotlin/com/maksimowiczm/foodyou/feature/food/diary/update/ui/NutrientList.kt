package com.maksimowiczm.foodyou.feature.food.diary.update.ui

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.shared.component.IncompleteFoodsList
import com.maksimowiczm.foodyou.feature.food.shared.ui.EnergyProgressIndicator
import com.maksimowiczm.foodyou.feature.food.shared.ui.NutrientList
import com.maksimowiczm.foodyou.feature.shared.ui.stringResourceWithWeight
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFood
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodRecipe
import com.maksimowiczm.foodyou.shared.domain.food.isComplete
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement

@Composable
internal fun NutrientList(
    food: DiaryFood,
    measurement: Measurement,
    modifier: Modifier = Modifier,
) {
    val weight = remember(food, measurement) { food.weight(measurement) }
    val facts = remember(food, weight) { food.nutritionFacts * (weight / 100) }

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
            ) ?: error("Invalid measurement: $measurement for ${food.name}")

        Text(
            text = measurementString,
            modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp),
            style = MaterialTheme.typography.labelLarge,
        )

        NutrientList(facts)

        if (food is DiaryFoodRecipe && !food.nutritionFacts.isComplete) {
            val foods =
                remember(food) {
                    food
                        .flatIngredients()
                        .filterNot { it.nutritionFacts.isComplete }
                        .map { it.name }
                }

            IncompleteFoodsList(foods = foods, modifier = Modifier.padding(8.dp))
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}
