package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.data.Food
import com.maksimowiczm.foodyou.feature.diary.data.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.diary.network.model.OpenFoodFactsProduct

data class Product(
    override val id: FoodId.Product,

    override val name: String,
    override val brand: String? = null,
    val barcode: String? = null,

    override val nutrients: Nutrients,

    /**
     * Amount of the product in the package in grams.
     */
    override val packageWeight: Float? = null,

    /**
     * Unit of the product serving in grams.
     */
    override val servingWeight: Float? = null,

    /**
     * Unit of the product quantity.
     */
    override val weightUnit: WeightUnit,

    /**
     * Source of the product data.
     */
    val productSource: ProductSource
) : Food

fun ProductEntity.toDomain(): Product = Product(
    id = FoodId.Product(id),
    name = name,
    brand = brand,
    barcode = barcode,
    nutrients = Nutrients(
        calories = calories,
        proteins = proteins,
        carbohydrates = carbohydrates,
        sugars = sugars.toNutrientValue(),
        fats = fats,
        saturatedFats = saturatedFats.toNutrientValue(),
        salt = salt.toNutrientValue(),
        sodium = sodium.toNutrientValue(),
        fiber = fiber.toNutrientValue()
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
