package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.IconButtonWidthOption
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.NutritionCalculator
import com.maksimowiczm.foodyou.shared.ui.LocalNutrientsPalette

private const val barHeight = 32

@Composable
fun NutrientsHeader(
    proteins: Weight?,
    carbohydrates: Weight?,
    fats: Weight?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (proteins == null || carbohydrates == null || fats == null) return

    val nutrientsPalette = LocalNutrientsPalette.current
    val proteinsKcal =
        remember(proteins) {
            NutritionCalculator.calculateProteinCalories(proteins).kilocalories.toFloat()
        }
    val carbsKcal =
        remember(carbohydrates) {
            NutritionCalculator.calculateCarbohydrateCalories(carbohydrates).kilocalories.toFloat()
        }
    val fatsKcal =
        remember(fats) {
            NutritionCalculator.calculateFatCalories(fats).kilocalories.toFloat()
        }
    val total =
        remember(proteinsKcal, carbsKcal, fatsKcal) {
            (proteinsKcal + carbsKcal + fatsKcal)
        }

    val rotationState =
        animateFloatAsState(
            targetValue = if (expanded || !enabled) 180f else 0f,
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f).clip(MaterialTheme.shapes.large),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (proteinsKcal > 0)
                Box(
                    Modifier.height(barHeight.dp)
                        .weight(proteinsKcal / total)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(nutrientsPalette.proteinsOnSurfaceContainer)
                )
            if (carbsKcal > 0)
                Box(
                    Modifier.height(barHeight.dp)
                        .weight(carbsKcal / total)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(nutrientsPalette.carbohydratesOnSurfaceContainer)
                )
            if (fatsKcal > 0)
                Box(
                    Modifier.height(barHeight.dp)
                        .weight(fatsKcal / total)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(nutrientsPalette.fatsOnSurfaceContainer)
                )
        }
        FilledIconToggleButton(
            checked = expanded,
            onCheckedChange = onExpandedChange,
            enabled = enabled,
            shapes = IconButtonDefaults.toggleableShapes(),
            modifier =
                Modifier.size(
                    IconButtonDefaults.extraSmallContainerSize(IconButtonWidthOption.Wide)
                ),
        ) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowUp,
                contentDescription = null,
                modifier =
                    Modifier.size(IconButtonDefaults.extraSmallIconSize).graphicsLayer {
                        rotationZ = rotationState.value
                    },
            )
        }
    }
}
