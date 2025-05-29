package com.maksimowiczm.foodyou.feature.reciperedesign.ui

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

internal class IngredientsSearchViewModel : ViewModel() {
    private val searchQuery = MutableStateFlow<String?>(null)

    val pages: Flow<PagingData<Ingredient>> = flowOf(
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
                )
            )
        )
    )

    fun onSearch(query: String?) {
        searchQuery.value = query
    }
}
