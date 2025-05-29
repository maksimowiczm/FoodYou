package com.maksimowiczm.foodyou.feature.recipe.model

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.Measurement.Gram
import com.maksimowiczm.foodyou.core.domain.model.Measurement.Serving
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.domain.model.RecipeIngredient

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

/**
 * Compares two lists of ingredients to check if they are equal.
 *
 * @param other The list of RecipeIngredient to compare with.
 * @return True if the lists are equal, false otherwise.
 */
internal fun List<Ingredient>.compare(other: List<RecipeIngredient>): Boolean {
    if (this.size != other.size) return false

    val thisMap = this.associateBy { it.product.id }
    val otherMap = other.associateBy { it.food.id }

    return thisMap.all { (key, value) ->
        val otherValue = otherMap[key]
        if (otherValue == null) return@all false

        value.measurement == otherValue.measurement
    }
}
