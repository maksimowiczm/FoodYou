package com.maksimowiczm.foodyou.feature.recipe.ui.create

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.domain.model.Recipe
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.feature.recipe.domain.CreateRecipeUseCase
import com.maksimowiczm.foodyou.feature.recipe.domain.Ingredient
import com.maksimowiczm.foodyou.feature.recipe.ui.MinimalIngredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class CreateRecipeViewModel(
    private val foodRepository: FoodRepository,
    private val createRecipeUseCase: CreateRecipeUseCase
) : ViewModel() {

    fun observeIngredients(minimalIngredients: List<MinimalIngredient>): Flow<List<Ingredient>> {
        if (minimalIngredients.isEmpty()) {
            return flowOf(emptyList())
        }

        val flows = minimalIngredients.map { internalIngredient ->
            foodRepository
                .observeFood(internalIngredient.foodId)
                .filterNotNull()
                .map {
                    when (it) {
                        is Product -> Ingredient.Product(
                            uniqueId = it.id.toString(),
                            food = it,
                            measurement = internalIngredient.measurement
                        )

                        is Recipe -> Ingredient.Recipe(
                            uniqueId = it.id.toString(),
                            food = it,
                            measurement = internalIngredient.measurement
                        )
                    }
                }
        }

        return flows.combine { it.toList() }
    }

    fun onSave(name: String, servings: Int, ingredients: List<Ingredient>) = launch {
        val id = createRecipeUseCase(
            name = name,
            servings = servings,
            ingredients = ingredients
        )
    }
}
