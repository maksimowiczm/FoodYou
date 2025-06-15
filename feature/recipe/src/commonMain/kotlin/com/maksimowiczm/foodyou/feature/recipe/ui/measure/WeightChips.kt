package com.maksimowiczm.foodyou.feature.recipe.ui.measure

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
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.stringResource

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
                filterOptions = listOfNotNull(
                    extraFilter,
                    if (food.isLiquid) Measurement.Milliliter(100f) else Measurement.Gram(100f),
                    food.totalWeight?.let { Measurement.Package(1f) },
                    food.servingWeight?.let { Measurement.Serving(1f) }
                ).distinct(),
                initialSelectedFilterIndex = it[0] as Int
            )
        }
    )
) {
    WeightChipsState(
        filterOptions = listOfNotNull(
            extraFilter,
            if (food.isLiquid) Measurement.Milliliter(100f) else Measurement.Gram(100f),
            food.totalWeight?.let { Measurement.Package(1f) },
            food.servingWeight?.let { Measurement.Serving(1f) }
        ).distinct(),
        initialSelectedFilterIndex = 0
    )
}

@Stable
internal class WeightChipsState(
    val filterOptions: List<Measurement> = emptyList(),
    initialSelectedFilterIndex: Int
) {
    var selectedFilterIndex: Int by mutableIntStateOf(initialSelectedFilterIndex)
    val selectedFilter by derivedStateOf { filterOptions[selectedFilterIndex] }
}
