package com.maksimowiczm.foodyou.feature.recipe.model

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Measurement.Gram
import com.maksimowiczm.foodyou.core.model.Measurement.Serving
import com.maksimowiczm.foodyou.core.model.Product

@Immutable
internal data class Ingredient(val product: Product, val measurement: Measurement) {
    val productId = product.id.id

    val weight: Float?
        get() = with(product) {
            when (measurement) {
                is Gram -> measurement.value
                is Measurement.Package -> packageWeight?.let { measurement.weight(packageWeight) }
                is Serving -> servingWeight?.let { measurement.weight(servingWeight) }
            }
        }
}
