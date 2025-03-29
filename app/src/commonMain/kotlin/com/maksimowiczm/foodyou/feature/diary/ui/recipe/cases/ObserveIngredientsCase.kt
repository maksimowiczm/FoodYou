package com.maksimowiczm.foodyou.feature.diary.ui.recipe.cases

import com.maksimowiczm.foodyou.ext.combine
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

class ObserveIngredientsCase(private val productRepository: ProductRepository) {
    /**
     * Observes a list of products by their IDs. It is guaranteed that the list will contain
     * products in the same order as the input list.
     */
    operator fun invoke(products: List<FoodId.Product>): Flow<List<Product>> = products
        .map {
            productRepository.observeProductById(it.productId).filterNotNull()
        }
        .combine {
            it.toList()
        }
}
