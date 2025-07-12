package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.CreateProductScreen
import com.maksimowiczm.foodyou.feature.food.ui.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.CreateMeasurementScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.FoodSearchScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsProductScreen
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class FoodSearch(val mealId: Long, val epochDay: Long) {
    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)
}

@Serializable
data class OpenFoodFactsProduct(val id: Long)

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
data class CreateProductMeasurement(val productId: Long, val mealId: Long, val date: Long) {
    constructor(
        foodId: FoodId.Product,
        mealId: Long,
        date: LocalDate
    ) : this(foodId.id, mealId, date.toEpochDays())

    val foodId: FoodId.Product
        get() = FoodId.Product(productId)
}

fun NavGraphBuilder.foodDiaryGraph(
    foodSearchOnBack: () -> Unit,
    foodSearchOnCreateProduct: (mealId: Long, date: LocalDate) -> Unit,
    foodSearchOnOpenFoodFactsProduct: (id: Long) -> Unit,
    foodSearchOnFood: (FoodId, mealId: Long, date: LocalDate) -> Unit,
    openFoodFactsProductOnBack: () -> Unit,
    openFoodFactsProductOnImport: (id: FoodId.Product) -> Unit,
    createProductOnBack: () -> Unit,
    createProductOnCreate: (FoodId.Product, mealId: Long, date: LocalDate) -> Unit,
    updateProductOnBack: () -> Unit,
    updateProductOnUpdate: () -> Unit,
    createMeasurementOnBack: () -> Unit,
    createMeasurementOnEditProduct: (FoodId.Product) -> Unit,
    createMeasurementOnDeleteProduct: () -> Unit,
    createMeasurementOnCreateMeasurement: () -> Unit
) {
    forwardBackwardComposable<FoodSearch> { backStack ->
        val route = backStack.toRoute<FoodSearch>()
        val mealId = route.mealId
        val date = route.date

        FoodSearchScreen(
            onBack = foodSearchOnBack,
            onCreateProduct = { foodSearchOnCreateProduct(mealId, date) },
            onOpenFoodFactsProduct = { foodSearchOnOpenFoodFactsProduct(it.id) },
            onFood = { foodSearchOnFood(it.id, mealId, date) },
            viewModel = koinViewModel(
                parameters = { parametersOf(mealId, date) }
            ),
            animatedVisibilityScope = this
        )
    }
    forwardBackwardComposable<OpenFoodFactsProduct> {
        val (id) = it.toRoute<OpenFoodFactsProduct>()

        OpenFoodFactsProductScreen(
            onBack = openFoodFactsProductOnBack,
            onImport = openFoodFactsProductOnImport,
            productId = id
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

        CreateMeasurementScreen(
            onBack = createMeasurementOnBack,
            onEdit = { createMeasurementOnEditProduct(route.foodId) },
            onDelete = createMeasurementOnDeleteProduct,
            onCreateMeasurement = createMeasurementOnCreateMeasurement,
            productId = route.foodId,
            mealId = route.mealId,
            animatedVisibilityScope = this
        )
    }
}
