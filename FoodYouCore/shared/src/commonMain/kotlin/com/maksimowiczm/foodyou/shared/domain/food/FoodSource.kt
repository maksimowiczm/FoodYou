package com.maksimowiczm.foodyou.shared.domain.food

import kotlinx.serialization.Serializable

@Serializable
data class FoodSource(val type: Type, val url: String? = null) {
    interface Type {
        companion object
    }
}
