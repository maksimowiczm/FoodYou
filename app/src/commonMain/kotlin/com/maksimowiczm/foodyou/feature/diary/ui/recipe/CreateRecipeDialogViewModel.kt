package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductSource
import com.maksimowiczm.foodyou.feature.diary.data.model.RecipeIngredient
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateRecipeDialogViewModel : ViewModel() {
    private var _ingredients = MutableStateFlow<List<RecipeIngredient>>(
        listOf(
            RecipeIngredient(
                id = -1,
                product = Product(
                    id = -1,
                    name = "Chicken",
                    brand = "KFC",
                    nutrients = Nutrients(
                        calories = 100f,
                        proteins = 20f,
                        carbohydrates = 0f,
                        fats = 2f
                    ),
                    weightUnit = WeightUnit.Gram,
                    productSource = ProductSource.User
                ),
                measurement = WeightMeasurement.WeightUnit(100f)
            ),
            RecipeIngredient(
                id = -1,
                product = Product(
                    id = -2,
                    name = "Rice",
                    brand = "Uncle Ben's",
                    nutrients = Nutrients(
                        calories = 200f,
                        proteins = 5f,
                        carbohydrates = 40f,
                        fats = 1f
                    ),
                    weightUnit = WeightUnit.Gram,
                    productSource = ProductSource.User,
                    servingWeight = 100f
                ),
                measurement = WeightMeasurement.Serving(1f, 100f)
            )
        )
    )

    val ingredients = _ingredients.asStateFlow()
}
