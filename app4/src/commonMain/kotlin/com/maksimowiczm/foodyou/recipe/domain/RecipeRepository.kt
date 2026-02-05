package com.maksimowiczm.foodyou.recipe.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.userfood.domain.FoodNote
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun search(parameters: RecipeSearchParameters, pageSize: Int): Flow<PagingData<Recipe>>

    fun count(parameters: RecipeSearchParameters): Flow<Int>

    suspend fun create(
        accountId: LocalAccountId,
        name: RecipeName,
        servings: Double,
        image: Image.Local?,
        note: FoodNote?,
        finalWeight: Double?,
        ingredients: List<RecipeIngredient>,
    ): RecipeIdentity

    suspend fun update(
        identity: RecipeIdentity,
        name: RecipeName,
        servings: Double,
        image: Image.Local?,
        note: FoodNote?,
        finalWeight: Double?,
        ingredients: List<RecipeIngredient>,
    )

    fun observe(identity: RecipeIdentity): Flow<Recipe?>

    suspend fun delete(identity: RecipeIdentity)
}
