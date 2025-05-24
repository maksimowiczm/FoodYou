package com.maksimowiczm.foodyou.feature.measurement

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.measurement.ui.CreateMeasurementScreen
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

        CreateMeasurementScreen(
            foodId = remember { route.foodId },
            mealId = remember { route.mealId },
            date = remember { LocalDate.fromEpochDays(route.epochDay) },
            onBack = measurementOnBack
        )
    }
}
