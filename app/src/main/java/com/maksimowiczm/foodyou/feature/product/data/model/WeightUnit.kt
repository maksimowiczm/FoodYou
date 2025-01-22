package com.maksimowiczm.foodyou.feature.product.data.model

enum class WeightUnit {
    Gram,
    Millilitre
}

fun WeightUnit.toGram(): Float {
    return when (this) {
        WeightUnit.Gram -> 1f
        WeightUnit.Millilitre -> 1f
    }
}
