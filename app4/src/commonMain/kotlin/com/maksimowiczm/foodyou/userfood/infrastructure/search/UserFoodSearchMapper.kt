package com.maksimowiczm.foodyou.userfood.infrastructure.search

import com.maksimowiczm.foodyou.userfood.domain.search.UserFoodSearchItem
import com.maksimowiczm.foodyou.userfood.infrastructure.product.ProductMapper
import com.maksimowiczm.foodyou.userfood.infrastructure.room.search.UserFoodSearchEntity

internal class UserFoodSearchMapper(private val productMapper: ProductMapper) {
    fun userFoodSearchItem(entity: UserFoodSearchEntity): UserFoodSearchItem {
        return when {
            entity.product != null ->
                UserFoodSearchItem.Product(productMapper.userProduct(entity.product))

            else -> TODO()
        }
    }
}
