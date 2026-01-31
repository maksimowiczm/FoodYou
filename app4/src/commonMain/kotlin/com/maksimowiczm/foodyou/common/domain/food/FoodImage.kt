package com.maksimowiczm.foodyou.common.domain.food

sealed interface FoodImage {
    data class Remote(val thumbnail: String?, val fullSize: String?) : FoodImage

    data class Local(val uri: String) : FoodImage
}
