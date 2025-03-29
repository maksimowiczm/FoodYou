package com.maksimowiczm.foodyou.feature.diary.ui.createrecipe

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.createrecipe.model.Ingredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateRecipeViewModel : ViewModel() {
    val ingredients: StateFlow<List<Ingredient>> = MutableStateFlow(emptyList())
}
