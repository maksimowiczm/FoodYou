package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.CreateProductScreen
import com.maksimowiczm.foodyou.feature.food.ui.CreateRecipeScreen
import com.maksimowiczm.foodyou.feature.food.ui.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.food.ui.UpdateRecipeScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodSearchScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.settings.DailyGoalsScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.cardsettings.MealsCardsSettings
import com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.settings.MealSettingsScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.CreateMeasurementScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.measure.UpdateMeasurementScreen
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.from
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type
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
data class CreateRecipe(val mealId: Long, val date: Long) {
    constructor(mealId: Long, date: LocalDate) : this(mealId, date.toEpochDays())

    val localDate: LocalDate
        get() = LocalDate.fromEpochDays(date)
}

@Serializable
data class UpdateRecipe(val recipeId: Long) {
    val foodId: FoodId.Recipe
        get() = FoodId.Recipe(recipeId)

    companion object {
        fun from(foodId: FoodId.Recipe) = UpdateRecipe(foodId.id)
    }
}

@Serializable
data class CreateMeasurement(
    val productId: Long?,
    val recipeId: Long?,
    val mealId: Long,
    val epochDay: Long,
    val measurementType: MeasurementType?,
    val quantity: Float?
) {
    constructor(
        foodId: FoodId,
        mealId: Long,
        date: LocalDate,
        measurement: Measurement?
    ) : this(
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
        mealId = mealId,
        epochDay = date.toEpochDays(),
        measurementType = measurement?.type,
        quantity = measurement?.rawValue
    )

    val foodId: FoodId
        get() = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> throw IllegalStateException("Either productId or recipeId must be provided")
        }

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

@Serializable
data object MealSettings

@Serializable
data object MealsCardsSettings

@Serializable
data object DailyGoals

fun NavGraphBuilder.foodDiaryGraph(
    foodSearchOnBack: () -> Unit,
    foodSearchOnCreateProduct: (mealId: Long, date: LocalDate) -> Unit,
    foodSearchOnCreateRecipe: (mealId: Long, date: LocalDate) -> Unit,
    foodSearchOnFood: (FoodId, Measurement, mealId: Long, date: LocalDate) -> Unit,
    createProductOnBack: () -> Unit,
    createProductOnCreate: (FoodId.Product, mealId: Long, date: LocalDate) -> Unit,
    createRecipeOnBack: () -> Unit,
    createRecipeOnCreate: (FoodId.Recipe, mealId: Long, date: LocalDate) -> Unit,
    updateProductOnBack: () -> Unit,
    updateProductOnUpdate: () -> Unit,
    updateRecipeOnBack: () -> Unit,
    updateRecipeOnUpdate: () -> Unit,
    createMeasurementOnBack: () -> Unit,
    createMeasurementOnEditFood: (FoodId) -> Unit,
    createMeasurementOnDeleteFood: () -> Unit,
    createMeasurementOnCreateMeasurement: () -> Unit,
    updateMeasurementOnBack: () -> Unit,
    updateMeasurementOnEdit: (FoodId) -> Unit,
    updateMeasurementOnDelete: () -> Unit,
    updateMeasurementOnUpdate: () -> Unit,
    mealSettingsOnBack: () -> Unit,
    mealSettingsOnMealsCardsSettings: () -> Unit,
    mealsCardsSettingsOnBack: () -> Unit,
    mealsCardsOnMealSettings: () -> Unit,
    dailyGoalsOnBack: () -> Unit,
    dailyGoalsOnSave: () -> Unit
) {
    forwardBackwardComposable<FoodSearch> { backStack ->
        val route = backStack.toRoute<FoodSearch>()
        val mealId = route.mealId
        val date = route.date

        FoodSearchScreen(
            onBack = foodSearchOnBack,
            onFoodClick = { foodId, measurement ->
                foodSearchOnFood(foodId, measurement, mealId, date)
            },
            onCreateProduct = { foodSearchOnCreateProduct(mealId, date) },
            onCreateRecipe = { foodSearchOnCreateRecipe(mealId, date) },
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
    forwardBackwardComposable<CreateMeasurement> {
        val route = it.toRoute<CreateMeasurement>()

        CreateMeasurementScreen(
            onBack = createMeasurementOnBack,
            onEdit = { createMeasurementOnEditFood(route.foodId) },
            onDelete = createMeasurementOnDeleteFood,
            onCreateMeasurement = createMeasurementOnCreateMeasurement,
            foodId = route.foodId,
            mealId = route.mealId,
            date = route.date,
            measurement = route.measurement,
            animatedVisibilityScope = this
        )
    }
    forwardBackwardComposable<UpdateProductMeasurement> {
        val route = it.toRoute<UpdateProductMeasurement>()

        UpdateMeasurementScreen(
            onBack = updateMeasurementOnBack,
            onEdit = updateMeasurementOnEdit,
            onDelete = updateMeasurementOnDelete,
            onUpdateMeasurement = updateMeasurementOnUpdate,
            measurementId = route.measurementId,
            animatedVisibilityScope = this
        )
    }
    forwardBackwardComposable<CreateRecipe> {
        val route = it.toRoute<CreateRecipe>()

        CreateRecipeScreen(
            onBack = createRecipeOnBack,
            onCreate = { id ->
                createRecipeOnCreate(id, route.mealId, route.localDate)
            }
        )
    }
    forwardBackwardComposable<UpdateRecipe> {
        val route = it.toRoute<UpdateRecipe>()

        UpdateRecipeScreen(
            id = route.foodId,
            onBack = updateRecipeOnBack,
            onUpdate = updateRecipeOnUpdate
        )
    }
    forwardBackwardComposable<MealSettings> {
        MealSettingsScreen(
            onBack = mealSettingsOnBack,
            onMealsCardsSettings = mealSettingsOnMealsCardsSettings
        )
    }
    forwardBackwardComposable<MealsCardsSettings> {
        MealsCardsSettings(
            onBack = mealsCardsSettingsOnBack,
            onMealSettings = mealsCardsOnMealSettings
        )
    }
    forwardBackwardComposable<DailyGoals> {
        DailyGoalsScreen(
            onBack = dailyGoalsOnBack,
            onSave = dailyGoalsOnSave
        )
    }
}
