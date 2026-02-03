package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.food.EnergyProgressIndicator

@Composable
internal fun NutrientsHeader(
    proteins: Float?,
    carbohydrates: Float?,
    fats: Float?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotationState =
        animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ViewList, contentDescription = null)
        }
        if (proteins != null && carbohydrates != null && fats != null) {
            EnergyProgressIndicator(
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                modifier = Modifier.weight(1f),
            )
        } else {
            EnergyProgressIndicator(
                energy = 1f,
                proteins = 0f,
                carbohydrates = 0f,
                fats = 0f,
                goal = 1f,
                modifier = Modifier.weight(1f),
            )
        }

        IconButton(
            onClick = { onExpandedChange(!expanded) },
            shapes = IconButtonDefaults.shapes(),
        ) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowUp,
                contentDescription = null,
                modifier = Modifier.graphicsLayer { rotationZ = rotationState.value },
            )
        }
    }
}
