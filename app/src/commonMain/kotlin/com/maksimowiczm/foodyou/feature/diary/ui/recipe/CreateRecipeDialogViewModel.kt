package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Ingredient(
    val name: String,
    val brand: String?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val weightMeasurement: WeightMeasurement
)

class CreateRecipeDialogViewModel : ViewModel() {
    private var _ingredients = MutableStateFlow<List<Ingredient>>(
        listOf()
    )
    val ingredients = _ingredients.asStateFlow()
}
