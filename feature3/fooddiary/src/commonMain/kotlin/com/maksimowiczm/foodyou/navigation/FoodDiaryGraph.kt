package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.CreateProductScreen
import com.maksimowiczm.foodyou.feature.food.ui.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodSearchScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts.OpenFoodFactsProductScreen
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class FoodSearch(val mealId: Long, val epochDay: Long) {
    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)
}

@Serializable
data class OpenFoodFactsProduct(val id: Long)

@Serializable
data object CreateProduct

@Serializable
data class UpdateProduct(val id: Long) {
    val productId: FoodId.Product
        get() = FoodId.Product(id)

    companion object {
        fun from(productId: FoodId.Product) = UpdateProduct(productId.id)
    }
}

fun NavGraphBuilder.foodDiaryGraph(
    foodSearchOnBack: () -> Unit,
    foodSearchOnCreateProduct: () -> Unit,
    foodSearchOnOpenFoodFactsProduct: (id: Long) -> Unit,
    foodSearchOnFood: (id: FoodId) -> Unit,
    openFoodFactsProductOnBack: () -> Unit,
    openFoodFactsProductOnImport: (id: FoodId.Product) -> Unit,
    createProductOnBack: () -> Unit,
    createProductOnCreate: (FoodId.Product) -> Unit,
    updateProductOnBack: () -> Unit,
    updateProductOnUpdate: () -> Unit
) {
    forwardBackwardComposable<FoodSearch> {
        val route = it.toRoute<FoodSearch>()
        val mealId = route.mealId
        val date = route.date

        FoodSearchScreen(
            mealId = mealId,
            date = date,
            onBack = foodSearchOnBack,
            onCreateProduct = foodSearchOnCreateProduct,
            onOpenFoodFactsProduct = foodSearchOnOpenFoodFactsProduct,
            animatedVisibilityScope = this,
            onFood = foodSearchOnFood
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
    forwardBackwardComposable<CreateProduct> {
        CreateProductScreen(
            onBack = createProductOnBack,
            onCreate = createProductOnCreate
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
}
