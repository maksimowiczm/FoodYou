package com.maksimowiczm.foodyou.feature.measurement

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

fun NavGraphBuilder.measurementGraph(
    createOnBack: () -> Unit,
    updateOnBack: () -> Unit,
    onEditFood: (FoodId) -> Unit
) {
    forwardBackwardComposable<CreateMeasurement> {
        val route = it.toRoute<CreateMeasurement>()

        CreateMeasurementScreen(
            foodId = route.foodId,
            mealId = route.mealId,
            date = route.date,
            onBack = createOnBack,
            onEditFood = onEditFood,
            animatedVisibilityScope = this
        )
    }
    forwardBackwardComposable<UpdateMeasurement> {
        val route = it.toRoute<UpdateMeasurement>()

        UpdateMeasurementScreen(
            measurementId = route.measurementId,
            onBack = updateOnBack,
            onEditFood = onEditFood,
            animatedVisibilityScope = this
        )
    }
}

@Serializable
data class UpdateMeasurement(val measurementId: Long)

@Serializable
data class CreateMeasurement(
    val productId: Long?,
    val recipeId: Long?,
    val mealId: Long?,
    val epochDay: Int
) {
    constructor(
        foodId: FoodId,
        mealId: Long?,
        epochDay: Int
    ) : this(
        productId = when (foodId) {
            is FoodId.Product -> foodId.id
            is FoodId.Recipe -> null
        },
        recipeId = when (foodId) {
            is FoodId.Recipe -> foodId.id
            is FoodId.Product -> null
        },
        mealId = mealId,
        epochDay = epochDay
    )

    init {
        require(productId != null || recipeId != null) {
            "Either productId or recipeId must be provided"
        }
    }

    val foodId: FoodId
        get() = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Either productId or recipeId must be provided")
        }

    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)
}
