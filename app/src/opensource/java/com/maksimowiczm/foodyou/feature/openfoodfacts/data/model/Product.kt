package com.maksimowiczm.foodyou.feature.openfoodfacts.data.model

import com.maksimowiczm.foodyou.feature.openfoodfacts.database.ProductEntity
import com.maksimowiczm.foodyou.feature.openfoodfacts.network.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.product.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.data.model.ProductSource
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    brand = brand,
    barcode = barcode,
    nutrients = Nutrients(
        calories = calories,
        proteins = proteins,
        carbohydrates = carbohydrates,
        sugars = sugars,
        fats = fats,
        saturatedFats = saturatedFats,
        salt = salt,
        sodium = sodium,
        fiber = fiber
    ),
    packageWeight = packageWeight,
    servingWeight = servingWeight,
    weightUnit = weightUnit,
    productSource = productSource
)

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
