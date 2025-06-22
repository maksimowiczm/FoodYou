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
import androidx.compose.material.icons.outlined.UnfoldLess
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.NutritionFacts
import com.maksimowiczm.foodyou.core.model.stringResource
import com.maksimowiczm.foodyou.core.ui.nutrition.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.core.ui.nutrition.CompactNutritionFactsList
import com.maksimowiczm.foodyou.core.ui.nutrition.NutritionFactsList
import com.maksimowiczm.foodyou.feature.measurement.preferences.NutrientsListSize
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AdvancedMeasurementSummary(
    measurement: Measurement,
    nutritionFacts: NutritionFacts,
    size: NutrientsListSize,
    onSizeChange: (NutrientsListSize) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                proteins = nutritionFacts.proteins.value,
                carbohydrates = nutritionFacts.carbohydrates.value,
                fats = nutritionFacts.fats.value,
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
            )

            IconButton(
                onClick = { onSizeChange(size.toggle()) }
            ) {
                Icon(
                    imageVector = when (size) {
                        NutrientsListSize.Compact -> Icons.Outlined.UnfoldMore
                        NutrientsListSize.Full -> Icons.Outlined.UnfoldLess
                    },
                    contentDescription = null
                )
            }
        }

        Text(
            text = stringResource(Res.string.in_x, measurement.stringResource()),
            style = MaterialTheme.typography.labelLarge
        )

        when (size) {
            NutrientsListSize.Compact -> CompactNutritionFactsList(nutritionFacts)
            NutrientsListSize.Full -> NutritionFactsList(nutritionFacts)
        }
    }
}
