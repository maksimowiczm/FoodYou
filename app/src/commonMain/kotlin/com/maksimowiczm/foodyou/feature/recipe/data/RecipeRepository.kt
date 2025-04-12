package com.maksimowiczm.foodyou.feature.recipe.data

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class RecipeRepository {
    fun queryProducts(query: String?): Flow<PagingData<Ingredient>> {
        // TODO
        return flowOf(PagingData.empty())
    }
}
