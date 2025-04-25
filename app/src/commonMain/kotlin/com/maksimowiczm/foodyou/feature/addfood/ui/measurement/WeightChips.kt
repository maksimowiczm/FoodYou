package com.maksimowiczm.foodyou.feature.addfood.ui.measurement

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
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.res.stringResource

@Composable
internal fun WeightChips(
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
                label = { Text(suggestion.stringResource()) }
            )
        }
    }
}

@Composable
internal fun rememberWeightChipsState(
    food: Food,
    extraFilter: Measurement? = null
): WeightChipsState = rememberSaveable(
    food,
    extraFilter,
    saver = Saver(
        save = {
            arrayListOf<Any>(
                it.selectedFilterIndex
            )
        },
        restore = {
            WeightChipsState(
                packageSuggestion = food.packageWeight?.let { Measurement.Package(1f) },
                servingSuggestion = food.servingWeight?.let { Measurement.Serving(1f) },
                extraFilter = extraFilter,
                initialSelectedFilterIndex = it[0] as Int
            )
        }
    )
) {
    WeightChipsState(
        packageSuggestion = food.packageWeight?.let { Measurement.Package(1f) },
        servingSuggestion = food.servingWeight?.let { Measurement.Serving(1f) },
        extraFilter = extraFilter,
        initialSelectedFilterIndex = 0
    )
}

@Stable
internal class WeightChipsState(
    packageSuggestion: Measurement.Package?,
    servingSuggestion: Measurement.Serving?,
    extraFilter: Measurement?,
    initialSelectedFilterIndex: Int
) {
    val filterOptions: List<Measurement> = listOfNotNull(
        extraFilter,
        Measurement.Gram(100f),
        packageSuggestion?.let { Measurement.Package(1f) },
        servingSuggestion?.let { Measurement.Serving(1f) }
    ).distinct()

    var selectedFilterIndex: Int by mutableIntStateOf(initialSelectedFilterIndex)

    val selectedFilter by derivedStateOf { filterOptions[selectedFilterIndex] }
}
