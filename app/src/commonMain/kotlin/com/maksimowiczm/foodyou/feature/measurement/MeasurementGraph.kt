package com.maksimowiczm.foodyou.feature.measurement

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.measurement.ui.CreateMeasurementScreen
import com.maksimowiczm.foodyou.feature.measurement.ui.UpdateMeasurementScreen
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

@Serializable
data class UpdateMeasurement(val productMeasurementId: Long?, val recipeMeasurementId: Long?) {
    val measurementId: MeasurementId
        get() = when {
            productMeasurementId != null -> MeasurementId.Product(productMeasurementId)
            recipeMeasurementId != null -> MeasurementId.Recipe(recipeMeasurementId)
            else -> error("Either productMeasurementId or recipeMeasurementId must be provided")
        }

    constructor(measurementId: MeasurementId) : this(
        productMeasurementId = when (measurementId) {
            is MeasurementId.Product -> measurementId.id
            is MeasurementId.Recipe -> null
        },
        recipeMeasurementId = when (measurementId) {
            is MeasurementId.Recipe -> measurementId.id
            is MeasurementId.Product -> null
        }
    )
}

fun NavGraphBuilder.measurementGraph(
    createMeasurementOnBack: () -> Unit,
    updateMeasurementOnBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    createMeasurementOnRecipeClone: (FoodId.Product) -> Unit,
    updateMeasurementOnRecipeClone: (FoodId.Product) -> Unit
) {
    forwardBackwardComposable<CreateMeasurement> {
        val route = it.toRoute<CreateMeasurement>()

        CreateMeasurementScreen(
            foodId = route.foodId,
            mealId = route.mealId,
            date = LocalDate.fromEpochDays(route.epochDay),
            onBack = createMeasurementOnBack,
            onEditFood = { onEditFood(route.foodId) },
            onRecipeClone = createMeasurementOnRecipeClone
        )
    }
    forwardBackwardComposable<UpdateMeasurement> {
        val route = it.toRoute<UpdateMeasurement>()

        UpdateMeasurementScreen(
            measurementId = remember { route.measurementId },
            onBack = updateMeasurementOnBack,
            onEditFood = onEditFood,
            onRecipeClone = updateMeasurementOnRecipeClone
        )
    }
}
