package com.maksimowiczm.foodyou.feature.importexport.domain

import com.maksimowiczm.foodyou.feature.food.data.database.food.Minerals
import com.maksimowiczm.foodyou.feature.food.data.database.food.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product
import com.maksimowiczm.foodyou.feature.food.data.database.food.Vitamins
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource

interface ProductFieldMapMapper {

    /**
     * Converts a map of product fields to a [Product] object.
     *
     * @param sourceType The type of food source from which the product is imported.
     * @param fieldMap A map where keys are [ProductField] and values are their corresponding string values.
     *
     * @return A [Product] object constructed from the provided field map.
     *
     * Throws an [IllegalArgumentException] if the required fields are missing or invalid.
     */
    fun toProduct(sourceType: FoodSource.Type, fieldMap: Map<ProductField, String?>): Product
}

internal class ProductFieldMapMapperImpl : ProductFieldMapMapper {
    override fun toProduct(
        sourceType: FoodSource.Type,
        fieldMap: Map<ProductField, String?>
    ): Product {
        val name = fieldMap[ProductField.NAME] ?: error("Product name is required")

        return Product(
            name = name,
            brand = fieldMap[ProductField.BRAND],
            barcode = fieldMap[ProductField.BARCODE],
            nutrients = Nutrients(
                energy = fieldMap[ProductField.ENERGY]?.toFloatOrNull(),
                proteins = fieldMap[ProductField.PROTEINS]?.toFloatOrNull(),
                fats = fieldMap[ProductField.FATS]?.toFloatOrNull(),
                saturatedFats = fieldMap[ProductField.SATURATED_FATS]?.toFloatOrNull(),
                transFats = fieldMap[ProductField.TRANS_FATS]?.toFloatOrNull(),
                monounsaturatedFats = fieldMap[ProductField.MONOUNSATURATED_FATS]?.toFloatOrNull(),
                polyunsaturatedFats = fieldMap[ProductField.POLYUNSATURATED_FATS]?.toFloatOrNull(),
                omega3 = fieldMap[ProductField.OMEGA3]?.toFloatOrNull(),
                omega6 = fieldMap[ProductField.OMEGA6]?.toFloatOrNull(),
                carbohydrates = fieldMap[ProductField.CARBOHYDRATES]?.toFloatOrNull(),
                sugars = fieldMap[ProductField.SUGARS]?.toFloatOrNull(),
                addedSugars = fieldMap[ProductField.ADDED_SUGARS]?.toFloatOrNull(),
                dietaryFiber = fieldMap[ProductField.DIETARY_FIBER]?.toFloatOrNull(),
                solubleFiber = fieldMap[ProductField.SOLUBLE_FIBER]?.toFloatOrNull(),
                insolubleFiber = fieldMap[ProductField.INSOLUBLE_FIBER]?.toFloatOrNull(),
                salt = fieldMap[ProductField.SALT]?.toFloatOrNull(),
                cholesterolMilli = fieldMap[ProductField.CHOLESTEROL_MILLI]?.toFloatOrNull(),
                caffeineMilli = fieldMap[ProductField.CAFFEINE_MILLI]?.toFloatOrNull()
            ),
            vitamins = Vitamins(
                vitaminAMicro = fieldMap[ProductField.VITAMIN_A_MICRO]?.toFloatOrNull(),
                vitaminB1Milli = fieldMap[ProductField.VITAMIN_B1_MILLI]?.toFloatOrNull(),
                vitaminB2Milli = fieldMap[ProductField.VITAMIN_B2_MILLI]?.toFloatOrNull(),
                vitaminB3Milli = fieldMap[ProductField.VITAMIN_B3_MILLI]?.toFloatOrNull(),
                vitaminB5Milli = fieldMap[ProductField.VITAMIN_B5_MILLI]?.toFloatOrNull(),
                vitaminB6Milli = fieldMap[ProductField.VITAMIN_B6_MILLI]?.toFloatOrNull(),
                vitaminB7Micro = fieldMap[ProductField.VITAMIN_B7_MICRO]?.toFloatOrNull(),
                vitaminB9Micro = fieldMap[ProductField.VITAMIN_B9_MICRO]?.toFloatOrNull(),
                vitaminB12Micro = fieldMap[ProductField.VITAMIN_B12_MICRO]?.toFloatOrNull(),
                vitaminCMilli = fieldMap[ProductField.VITAMIN_C_MILLI]?.toFloatOrNull(),
                vitaminDMicro = fieldMap[ProductField.VITAMIN_D_MICRO]?.toFloatOrNull(),
                vitaminEMilli = fieldMap[ProductField.VITAMIN_E_MILLI]?.toFloatOrNull(),
                vitaminKMicro = fieldMap[ProductField.VITAMIN_K_MICRO]?.toFloatOrNull()
            ),
            minerals = Minerals(
                manganeseMilli = fieldMap[ProductField.MANGANESE_MILLI]?.toFloatOrNull(),
                magnesiumMilli = fieldMap[ProductField.MAGNESIUM_MILLI]?.toFloatOrNull(),
                potassiumMilli = fieldMap[ProductField.POTASSIUM_MILLI]?.toFloatOrNull(),
                calciumMilli = fieldMap[ProductField.CALCIUM_MILLI]?.toFloatOrNull(),
                copperMilli = fieldMap[ProductField.COPPER_MILLI]?.toFloatOrNull(),
                zincMilli = fieldMap[ProductField.ZINC_MILLI]?.toFloatOrNull(),
                sodiumMilli = fieldMap[ProductField.SODIUM_MILLI]?.toFloatOrNull(),
                ironMilli = fieldMap[ProductField.IRON_MILLI]?.toFloatOrNull(),
                phosphorusMilli = fieldMap[ProductField.PHOSPHORUS_MILLI]?.toFloatOrNull(),
                seleniumMicro = fieldMap[ProductField.SELENIUM_MICRO]?.toFloatOrNull(),
                iodineMicro = fieldMap[ProductField.IODINE_MICRO]?.toFloatOrNull(),
                chromiumMicro = fieldMap[ProductField.CHROMIUM_MICRO]?.toFloatOrNull()
            ),
            packageWeight = fieldMap[ProductField.PACKAGE_WEIGHT]?.toFloatOrNull(),
            servingWeight = fieldMap[ProductField.SERVING_WEIGHT]?.toFloatOrNull(),
            note = fieldMap[ProductField.NOTE],
            sourceType = sourceType,
            sourceUrl = fieldMap[ProductField.SOURCE_URL]
        )
    }
}
