package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.food.domain.Product
import com.maksimowiczm.foodyou.feature.food.domain.weight
import com.maksimowiczm.foodyou.feature.food.ui.EnergyProgressIndicator
import com.maksimowiczm.foodyou.feature.food.ui.NutrientList
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.unit_gram_short
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductMeasurementForm(
    state: ProductMeasurementFormState,
    product: Product,
    modifier: Modifier = Modifier
) {
    val measurement = state.measurementState.measurement

    val facts = remember(product, measurement) {
        val weight = measurement.weight(product)
            ?: error("Invalid measurement: $measurement for product: ${product.name}")
        product.nutritionFacts * (weight / 100)
    }

    Column(
        modifier = modifier
    ) {
        ChipsDatePicker(
            state = state.dateState,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        ChipsMealPicker(
            state = state.mealsState,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        MeasurementPicker(
            state = state.measurementState,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
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

            EnergyProgressIndicator(
                proteins = facts.proteins.value,
                carbohydrates = facts.carbohydrates.value,
                fats = facts.fats.value,
                modifier = Modifier.weight(1f)
            )
        }

        val weight = measurement.weight(product)
            ?: error("Invalid measurement: $measurement for product: ${product.name}")

        val text = buildString {
            append(measurement.stringResource())

            when (measurement) {
                is Measurement.Gram,
                is Measurement.Milliliter -> Unit

                is Measurement.Package,
                is Measurement.Serving -> append(
                    " (${weight.formatClipZeros()} ${stringResource(Res.string.unit_gram_short)})"
                )
            }
        }

        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )

        NutrientList(facts)
    }
}
