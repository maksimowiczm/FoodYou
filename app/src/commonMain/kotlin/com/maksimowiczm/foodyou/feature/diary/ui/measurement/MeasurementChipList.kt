package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightUnit
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.product_package
import foodyou.app.generated.resources.product_serving
import org.jetbrains.compose.resources.stringResource

@Composable
fun MeasurementChipList(
    state: MeasurementChipListState,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier.Companion
) {
    LazyRow(
        modifier = modifier
    ) {
        item {
            Spacer(
                Modifier.Companion.width(
                    paddingValues.calculateStartPadding(
                        LocalLayoutDirection.current
                    )
                )
            )
        }

        itemsIndexed(state.filterOptions) { i, filterOption ->
            Row {
                FilterChip(
                    selected = i == state.selectedFilterIndex,
                    onClick = { state.selectedFilterIndex = i },
                    label = { Text(filterOption.stringResource(state.product.weightUnit)) }
                )

                if (i < state.filterOptions.size - 1) {
                    Spacer(Modifier.Companion.width(8.dp))
                }
            }
        }

        item {
            Spacer(
                Modifier.Companion.width(
                    paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                )
            )
        }
    }
}

@Composable
private fun WeightMeasurement.stringResource(weightUnit: WeightUnit) = when (this) {
    is WeightMeasurement.WeightUnit -> "${weight.formatClipZeros()} " +
        weightUnit.stringResourceShort()

    is WeightMeasurement.Package -> "${quantity.formatClipZeros()} x " +
        stringResource(Res.string.product_package)

    is WeightMeasurement.Serving -> "${quantity.formatClipZeros()} x " +
        stringResource(Res.string.product_serving)
}
