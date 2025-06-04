package com.maksimowiczm.foodyou.feature.measurement.ui.advanced

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ChipsMeasurementPicker(
    measurements: List<String>,
    selectedMeasurement: Int?,
    onMeasurementSelect: (index: Int) -> Unit,
    onChooseOtherMeasurement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_weight),
                contentDescription = null
            )
        }

        Spacer(Modifier.width(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            measurements.forEachIndexed { i, measurement ->
                InputChip(
                    selected = selectedMeasurement == i,
                    onClick = { onMeasurementSelect(i) },
                    label = { Text(measurement) }
                )
            }

            InputChip(
                selected = false,
                onClick = onChooseOtherMeasurement,
                label = { Text(stringResource(Res.string.action_choose_other_measurement)) }
            )
        }
    }
}
