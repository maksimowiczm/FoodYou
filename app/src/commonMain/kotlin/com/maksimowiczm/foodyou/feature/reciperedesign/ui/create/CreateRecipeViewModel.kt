package com.maksimowiczm.foodyou.feature.reciperedesign.ui.create

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.domain.model.Recipe
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.feature.reciperedesign.domain.Ingredient
import com.maksimowiczm.foodyou.feature.reciperedesign.ui.MinimalIngredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class CreateRecipeViewModel(val foodRepository: FoodRepository) : ViewModel() {

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
}
