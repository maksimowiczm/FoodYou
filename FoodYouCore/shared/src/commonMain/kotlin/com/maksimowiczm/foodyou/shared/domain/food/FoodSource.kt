package com.maksimowiczm.foodyou.shared.domain.food

data class FoodSource(val type: Type, val url: String? = null) {
    interface Type {
        companion object
    }
}
