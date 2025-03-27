package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.data.model.RecipeIngredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateRecipeDialogViewModel : ViewModel() {
    private var _ingredients = MutableStateFlow<List<RecipeIngredient>>(
        listOf()
    )
    val ingredients = _ingredients.asStateFlow()
}
