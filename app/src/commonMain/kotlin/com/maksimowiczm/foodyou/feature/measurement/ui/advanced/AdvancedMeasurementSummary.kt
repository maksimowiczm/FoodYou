package com.maksimowiczm.foodyou.feature.measurement.ui.advanced

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.domain.model.Nutrients
import com.maksimowiczm.foodyou.core.ui.component.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.core.ui.component.NutrientsList

@Composable
fun AdvancedMeasurementSummary(nutrients: Nutrients, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ViewList,
                    contentDescription = null
                )
            }

            CaloriesProgressIndicator(
                proteins = nutrients.proteins.value,
                carbohydrates = nutrients.carbohydrates.value,
                fats = nutrients.fats.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
        }

        NutrientsList(
            nutrients = nutrients
        )
    }
}
