package com.maksimowiczm.foodyou.feature.measurement

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.measurement.ui.MeasurementScreen
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class FoodMeasurement(
    val productId: Long?,
    val recipeId: Long?,
    val mealId: Long?,
    val epochDay: Int
) {
    val foodId: FoodId
        get() = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Either productId or recipeId must be provided")
        }
}

fun NavGraphBuilder.measurementGraph(measurementOnBack: () -> Unit) {
    forwardBackwardComposable<FoodMeasurement> {
        val route = it.toRoute<FoodMeasurement>()

        val date = LocalDate.fromEpochDays(route.epochDay)

        MeasurementScreen(
            foodId = route.foodId,
            mealId = route.mealId,
            date = date,
            onBack = measurementOnBack
        )
    }
}
