package com.maksimowiczm.foodyou.userfood.domain.search

import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct

sealed interface UserFoodSearchItem {
    data class Product(val product: UserProduct) : UserFoodSearchItem
}
