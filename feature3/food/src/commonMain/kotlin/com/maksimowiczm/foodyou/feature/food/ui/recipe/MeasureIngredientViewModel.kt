package com.maksimowiczm.foodyou.feature.food.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.Food
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.Product
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.Recipe
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.coroutines.flow.stateIn

internal class MeasureIngredientViewModel(
    foodId: FoodId,
    foodDatabase: FoodDatabase,
    productMapper: ProductMapper,
    observeRecipeUseCase: ObserveRecipeUseCase
) : ViewModel() {
    val productDao = foodDatabase.productDao

    val food: StateFlow<Food?> = when (foodId) {
        is FoodId.Product ->
            productDao
                .observe(foodId.id)
                .mapIfNotNull(productMapper::toModel)

        is FoodId.Recipe -> observeRecipeUseCase(foodId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    val possibleMeasurements: StateFlow<Set<MeasurementType>?> = food
        .filterNotNull()
        .map {
            when (it) {
                is Product -> setOfNotNull(
                    MeasurementType.Gram,
                    MeasurementType.Milliliter,
                    if (it.packageWeight != null) MeasurementType.Package else null,
                    if (it.servingWeight != null) MeasurementType.Serving else null
                )

                is Recipe -> setOfNotNull(
                    MeasurementType.Gram,
                    MeasurementType.Milliliter,
                    MeasurementType.Package,
                    MeasurementType.Serving
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

    val suggestions: StateFlow<Set<Measurement>?> = possibleMeasurements
        .filterNotNull()
        .map { set ->
            set.map { type ->
                when (type) {
                    MeasurementType.Gram -> Measurement.Gram(100f)
                    MeasurementType.Package -> Measurement.Package(1f)
                    MeasurementType.Serving -> Measurement.Serving(1f)
                    MeasurementType.Milliliter -> Measurement.Milliliter(100f)
                }
            }.toSet()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )
}
