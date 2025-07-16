package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.CreateProductScreen
import com.maksimowiczm.foodyou.feature.food.ui.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.fooddiary.domain.from
import com.maksimowiczm.foodyou.feature.fooddiary.domain.rawValue
import com.maksimowiczm.foodyou.feature.fooddiary.domain.type
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodSearchScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.CreateProductMeasurementScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.UpdateProductMeasurementScreen
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class FoodSearch(val mealId: Long, val epochDay: Long) {
    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)
}

@Serializable
data class CreateProduct(val mealId: Long, val date: Long) {
    constructor(mealId: Long, date: LocalDate) : this(mealId, date.toEpochDays())

    val localDate: LocalDate
        get() = LocalDate.fromEpochDays(date)
}

@Serializable
data class UpdateProduct(val id: Long) {
    val productId: FoodId.Product
        get() = FoodId.Product(id)

    companion object {
        fun from(productId: FoodId.Product) = UpdateProduct(productId.id)
    }
}

@Serializable
data class CreateProductMeasurement(
    val productId: Long,
    val mealId: Long,
    val epochDay: Long,
    val measurementType: MeasurementType?,
    val quantity: Float?
) {
    constructor(
        foodId: FoodId.Product,
        mealId: Long,
        date: LocalDate,
        measurement: Measurement?
    ) : this(foodId.id, mealId, date.toEpochDays(), measurement?.type, measurement?.rawValue)

    val foodId: FoodId.Product
        get() = FoodId.Product(productId)

    val measurement: Measurement?
        get() = if (measurementType != null && quantity != null) {
            Measurement.from(measurementType, quantity)
        } else {
            null
        }

    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)
}

@Serializable
data class UpdateProductMeasurement(val measurementId: Long)

fun NavGraphBuilder.foodDiaryGraph(
    foodSearchOnBack: () -> Unit,
    foodSearchOnCreateProduct: (mealId: Long, date: LocalDate) -> Unit,
    foodSearchOnFood: (FoodId, Measurement, mealId: Long, date: LocalDate) -> Unit,
    createProductOnBack: () -> Unit,
    createProductOnCreate: (FoodId.Product, mealId: Long, date: LocalDate) -> Unit,
    updateProductOnBack: () -> Unit,
    updateProductOnUpdate: () -> Unit,
    createMeasurementOnBack: () -> Unit,
    createMeasurementOnEditProduct: (FoodId.Product) -> Unit,
    createMeasurementOnDeleteProduct: () -> Unit,
    createMeasurementOnCreateMeasurement: () -> Unit,
    updateMeasurementOnBack: () -> Unit,
    updateMeasurementOnEdit: (FoodId) -> Unit,
    updateMeasurementOnDelete: () -> Unit,
    updateMeasurementOnUpdate: () -> Unit
) {
    forwardBackwardComposable<FoodSearch> { backStack ->
        val route = backStack.toRoute<FoodSearch>()
        val mealId = route.mealId
        val date = route.date

        FoodSearchScreen(
            onBack = foodSearchOnBack,
            onCreateProduct = { foodSearchOnCreateProduct(mealId, date) },
            onFoodClick = { foodId, measurement ->
                foodSearchOnFood(foodId, measurement, mealId, date)
            },
            mealId = mealId,
            date = date,
            animatedVisibilityScope = this
        )
    }
    forwardBackwardComposable<CreateProduct> { backStack ->
        val route = backStack.toRoute<CreateProduct>()

        CreateProductScreen(
            onBack = createProductOnBack,
            onCreate = {
                createProductOnCreate(it, route.mealId, route.localDate)
            }
        )
    }
    forwardBackwardComposable<UpdateProduct> {
        val route = it.toRoute<UpdateProduct>()

        UpdateProductScreen(
            onBack = updateProductOnBack,
            onUpdate = updateProductOnUpdate,
            productId = route.productId
        )
    }
    forwardBackwardComposable<CreateProductMeasurement> {
        val route = it.toRoute<CreateProductMeasurement>()

        CreateProductMeasurementScreen(
            onBack = createMeasurementOnBack,
            onEdit = { createMeasurementOnEditProduct(route.foodId) },
            onDelete = createMeasurementOnDeleteProduct,
            onCreateMeasurement = createMeasurementOnCreateMeasurement,
            productId = route.foodId,
            mealId = route.mealId,
            date = route.date,
            measurement = route.measurement,
            animatedVisibilityScope = this
        )
    }
    forwardBackwardComposable<UpdateProductMeasurement> {
        val route = it.toRoute<UpdateProductMeasurement>()

        UpdateProductMeasurementScreen(
            onBack = updateMeasurementOnBack,
            onEdit = updateMeasurementOnEdit,
            onDelete = updateMeasurementOnDelete,
            onUpdateMeasurement = updateMeasurementOnUpdate,
            measurementId = route.measurementId,
            animatedVisibilityScope = this
        )
    }
}
