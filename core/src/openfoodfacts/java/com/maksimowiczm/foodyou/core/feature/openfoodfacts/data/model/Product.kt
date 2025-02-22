package com.maksimowiczm.foodyou.core.feature.openfoodfacts.data.model

import com.maksimowiczm.foodyou.core.feature.openfoodfacts.network.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.feature.product.data.model.ProductSource
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit
import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity

/**
 * Converts an [OpenFoodFactsProduct] to a [Product]. Returns null if the conversion is not possible.
 */
internal fun OpenFoodFactsProduct.toEntity(): ProductEntity? {
    val packageQuantityUnit = packageQuantityUnit?.toWeightUnit()
    val servingQuantityUnit = servingQuantityUnit?.toWeightUnit()

    if (
        packageQuantityUnit != null &&
        servingQuantityUnit != null &&
        packageQuantityUnit != servingQuantityUnit
    ) {
        return null
    }

    val weightUnit = packageQuantityUnit ?: WeightUnit.Gram

    if (listOf(
            nutrients.energy100g,
            nutrients.proteins100g,
            nutrients.carbohydrates100g,
            nutrients.fat100g,
            code
        ).any { it == null }
    ) {
        return null
    }

    return ProductEntity(
        name = productName,
        brand = brands,
        barcode = code,
        calories = nutrients.energy100g!!,
        proteins = nutrients.proteins100g!!,
        carbohydrates = nutrients.carbohydrates100g!!,
        sugars = nutrients.sugars100g,
        fats = nutrients.fat100g!!,
        saturatedFats = nutrients.saturatedFat100g,
        salt = nutrients.salt100g,
        sodium = nutrients.sodium100g,
        fiber = nutrients.fiber100g,
        packageWeight = packageQuantity,
        servingWeight = servingQuantity,
        weightUnit = weightUnit,
        productSource = ProductSource.OpenFoodFacts
    )
}

private fun String.toWeightUnit(): WeightUnit? = when (this) {
    "g" -> WeightUnit.Gram
    "ml" -> WeightUnit.Milliliter
    else -> null
}
