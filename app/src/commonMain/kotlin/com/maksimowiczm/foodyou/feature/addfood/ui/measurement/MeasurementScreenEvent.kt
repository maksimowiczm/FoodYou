package com.maksimowiczm.foodyou.feature.addfood.ui.measurement

import com.maksimowiczm.foodyou.core.domain.model.FoodId

internal sealed interface MeasurementScreenEvent {
    data object Closed : MeasurementScreenEvent
    data class RecipeClonedIntoProduct(val newId: FoodId.Product) : MeasurementScreenEvent
    data object FoodDeleted : MeasurementScreenEvent
}
