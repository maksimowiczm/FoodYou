package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrient
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement

@Composable
fun rememberNutrientsListState(
    product: Product,
    extraFilters: List<WeightMeasurement> = emptyList()
): NutrientsListState = rememberSaveable(
    product,
    extraFilters,
    saver = Saver(
        save = {
            arrayListOf<Any>(
                it.selectedFilterIndex
            )
        },
        restore = {
            NutrientsListState(
                product = product,
                extraFilters = extraFilters,
                initialSelectedFilterIndex = it[0] as Int
            )
        }
    )
) {
    NutrientsListState(
        product = product,
        extraFilters = extraFilters,
        initialSelectedFilterIndex = 0
    )
}

@Stable
class NutrientsListState(
    val product: Product,
    extraFilters: List<WeightMeasurement>,
    initialSelectedFilterIndex: Int
) {
    val filterOptions: List<WeightMeasurement> = (
        extraFilters + listOfNotNull(
            WeightMeasurement.WeightUnit(
                weight = 100f
            ),
            product.packageWeight?.let {
                WeightMeasurement.Package(
                    quantity = 1f,
                    packageWeight = it
                )
            },
            product.servingWeight?.let {
                WeightMeasurement.Serving(
                    quantity = 1f,
                    servingWeight = it
                )
            }
        )
        ).distinct()

    var selectedFilterIndex: Int by mutableIntStateOf(initialSelectedFilterIndex)

    val selectedFilter by derivedStateOf { filterOptions[selectedFilterIndex] }

    val nutrients by derivedStateOf {
        Nutrients(
            calories = product.nutrients.calories(selectedFilter.weight),
            proteins = product.nutrients.proteins(selectedFilter.weight),
            carbohydrates = product.nutrients.carbohydrates(selectedFilter.weight),
            sugars = product.nutrients.get(Nutrient.Sugars, selectedFilter.weight),
            fats = product.nutrients.fats(selectedFilter.weight),
            saturatedFats = product.nutrients.get(Nutrient.SaturatedFats, selectedFilter.weight),
            salt = product.nutrients.get(Nutrient.Salt, selectedFilter.weight),
            sodium = product.nutrients.get(Nutrient.Sodium, selectedFilter.weight),
            fiber = product.nutrients.get(Nutrient.Fiber, selectedFilter.weight)
        )
    }
}
