package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.model.Product
import com.maksimowiczm.foodyou.feature.diary.ui.res.stringResource

@Composable
fun WeightChips(
    state: WeightChipsState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        itemsIndexed(state.filterOptions) { i, suggestion ->
            FilterChip(
                selected = i == state.selectedFilterIndex,
                onClick = { state.selectedFilterIndex = i },
                label = {
                    Text(suggestion.stringResource())
                }
            )
        }
    }
}

@Composable
fun rememberWeightChipsState(
    product: Product,
    extraFilter: WeightMeasurement? = null
): WeightChipsState = rememberSaveable(
    product,
    extraFilter,
    saver = Saver(
        save = {
            arrayListOf<Any>(
                it.selectedFilterIndex
            )
        },
        restore = {
            WeightChipsState(
                packageSuggestion = product.packageSuggestion,
                servingSuggestion = product.servingSuggestion,
                extraFilter = extraFilter,
                initialSelectedFilterIndex = it[0] as Int
            )
        }
    )
) {
    WeightChipsState(
        packageSuggestion = product.packageSuggestion,
        servingSuggestion = product.servingSuggestion,
        extraFilter = extraFilter,
        initialSelectedFilterIndex = 0
    )
}

@Stable
class WeightChipsState(
    packageSuggestion: WeightMeasurement.Package?,
    servingSuggestion: WeightMeasurement.Serving?,
    extraFilter: WeightMeasurement?,
    initialSelectedFilterIndex: Int
) {
    val filterOptions: List<WeightMeasurement> = (
        listOfNotNull(
            extraFilter,
            WeightMeasurement.WeightUnit(
                weight = 100f
            ),
            packageSuggestion?.copy(
                quantity = 1f
            ),
            servingSuggestion?.copy(
                quantity = 1f
            )
        )
        ).distinct()

    var selectedFilterIndex: Int by mutableIntStateOf(initialSelectedFilterIndex)

    val selectedFilter by derivedStateOf { filterOptions[selectedFilterIndex] }
}
