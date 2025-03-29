package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.model.Ingredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateRecipeViewModel(

) : ViewModel() {
    val ingredients: StateFlow<List<Ingredient>> = MutableStateFlow(
        listOf(
            Ingredient(
                productId = FoodId.Product(2L)
            )
        )
    )


}
