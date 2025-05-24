package com.maksimowiczm.foodyou.feature.measurement.ui

import com.maksimowiczm.foodyou.core.domain.model.FoodId

sealed interface MeasurementScreenEvent {
    /**
     * Work here is done and the screen should be closed.
     */
    data object Done : MeasurementScreenEvent

    /**
     * The food was deleted.
     */
    data object Deleted : MeasurementScreenEvent

    /**
     * The recipe was cloned into a new product.
     */
    data class RecipeCloned(val productId: FoodId.Product) : MeasurementScreenEvent
}
