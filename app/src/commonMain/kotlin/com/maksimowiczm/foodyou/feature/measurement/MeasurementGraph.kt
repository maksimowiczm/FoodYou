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
data class CreateMeasurement(
    val productId: Long?,
    val recipeId: Long?,
    val mealId: Long?,
    val epochDay: Int
) {
    constructor(
        foodId: FoodId?,
        mealId: Long?,
        epochDay: Int
    ) : this(
        productId = when (foodId) {
            is FoodId.Product -> foodId.id
            is FoodId.Recipe -> null
            null -> null
        },
        recipeId = when (foodId) {
            is FoodId.Recipe -> foodId.id
            is FoodId.Product -> null
            null -> null
        },
        mealId = mealId,
        epochDay = epochDay
    )

    val foodId: FoodId
        get() = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Either productId or recipeId must be provided")
        }
}

fun NavGraphBuilder.measurementGraph(measurementOnBack: () -> Unit) {
    forwardBackwardComposable<CreateMeasurement> {
        val route = it.toRoute<CreateMeasurement>()

        CreateMeasurementScreen(
            foodId = remember { route.foodId },
            mealId = remember { route.mealId },
            date = remember { LocalDate.fromEpochDays(route.epochDay) },
            onBack = measurementOnBack
        )
    }
}
