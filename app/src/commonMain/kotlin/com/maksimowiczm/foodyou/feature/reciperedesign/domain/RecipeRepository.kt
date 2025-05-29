package com.maksimowiczm.foodyou.feature.reciperedesign.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.Recipe
import com.maksimowiczm.foodyou.core.domain.model.RecipeIngredient
import com.maksimowiczm.foodyou.feature.reciperedesign.ui.testNutritionFacts
import com.maksimowiczm.foodyou.feature.reciperedesign.ui.testProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class RecipeRepository {
    fun queryIngredients(query: String?): Flow<PagingData<Ingredient>> = flowOf(
        PagingData.from(
            listOf(
                Ingredient.Product(
                    uniqueId = "1",
                    food = testProduct(),
                    measurement = Measurement.Gram(100f)
                ),
                Ingredient.Product(
                    uniqueId = "2",
                    food = testProduct(
                        name = "Another Product",
                        brand = "Brand B",
                        nutritionFacts = testNutritionFacts(
                            sodiumMilli = null
                        )
                    ),
                    measurement = Measurement.Serving(2f)
                ),
                Ingredient.Recipe(
                    uniqueId = "3",
                    food = Recipe(
                        id = FoodId.Recipe(1L),
                        name = "Test Recipe",
                        servings = 4,
                        ingredients = listOf(
                            RecipeIngredient(
                                food = testProduct(
                                    name = "Another Product",
                                    brand = "Brand B",
                                    nutritionFacts = testNutritionFacts(
                                        sodiumMilli = null
                                    )
                                ),
                                measurement = Measurement.Gram(150f)
                            )
                        )
                    ),
                    measurement = Measurement.Package(1f)
                )
            )
        )
    )
}
