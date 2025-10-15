package com.maksimowiczm.foodyou.food.domain

sealed interface FoodImage {
    data class Remote(val thumbnail: String?, val fullSize: String?) : FoodImage
}
