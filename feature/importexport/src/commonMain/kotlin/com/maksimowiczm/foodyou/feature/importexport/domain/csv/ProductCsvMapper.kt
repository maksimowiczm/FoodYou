package com.maksimowiczm.foodyou.feature.importexport.domain.csv

import com.maksimowiczm.foodyou.core.database.food.Minerals
import com.maksimowiczm.foodyou.core.database.food.Nutrients
import com.maksimowiczm.foodyou.core.database.food.ProductEntity
import com.maksimowiczm.foodyou.core.database.food.Vitamins

internal object ProductCsvMapper {
    fun toStringMap(product: ProductEntity): Map<ProductEntityField, String?> =
        product.toStringMap()

    fun toProductEntity(map: Map<ProductEntityField, String>): ProductEntity? =
        map.toProductEntity()
}

private fun ProductEntity.toStringMap(): Map<ProductEntityField, String?> = mapOf(
    ProductEntityField.NAME to name.toString(),
    ProductEntityField.BRAND to brand?.toString(),
    ProductEntityField.BARCODE to barcode?.toString(),
    ProductEntityField.PROTEINS to nutrients.proteins.toString(),
    ProductEntityField.CARBOHYDRATES to nutrients.carbohydrates.toString(),
    ProductEntityField.FATS to nutrients.fats.toString(),
    ProductEntityField.CALORIES to nutrients.calories.toString(),
    ProductEntityField.SATURATED_FATS to nutrients.saturatedFats?.toString(),
    ProductEntityField.MONOUNSATURATED_FATS to nutrients.monounsaturatedFats?.toString(),
    ProductEntityField.POLYUNSATURATED_FATS to nutrients.polyunsaturatedFats?.toString(),
    ProductEntityField.OMEGA3 to nutrients.omega3?.toString(),
    ProductEntityField.OMEGA6 to nutrients.omega6?.toString(),
    ProductEntityField.SUGARS to nutrients.sugars?.toString(),
    ProductEntityField.SALT to nutrients.salt?.toString(),
    ProductEntityField.FIBER to nutrients.fiber?.toString(),
    ProductEntityField.CHOLESTEROL_MILLI to nutrients.cholesterolMilli?.toString(),
    ProductEntityField.CAFFEINE_MILLI to nutrients.caffeineMilli?.toString(),
    ProductEntityField.VITAMIN_A_MICRO to vitamins.vitaminAMicro?.toString(),
    ProductEntityField.VITAMIN_B1_MILLI to vitamins.vitaminB1Milli?.toString(),
    ProductEntityField.VITAMIN_B2_MILLI to vitamins.vitaminB2Milli?.toString(),
    ProductEntityField.VITAMIN_B3_MILLI to vitamins.vitaminB3Milli?.toString(),
    ProductEntityField.VITAMIN_B5_MILLI to vitamins.vitaminB5Milli?.toString(),
    ProductEntityField.VITAMIN_B6_MILLI to vitamins.vitaminB6Milli?.toString(),
    ProductEntityField.VITAMIN_B7_MICRO to vitamins.vitaminB7Micro?.toString(),
    ProductEntityField.VITAMIN_B9_MICRO to vitamins.vitaminB9Micro?.toString(),
    ProductEntityField.VITAMIN_B12_MICRO to vitamins.vitaminB12Micro?.toString(),
    ProductEntityField.VITAMIN_C_MILLI to vitamins.vitaminCMilli?.toString(),
    ProductEntityField.VITAMIN_D_MICRO to vitamins.vitaminDMicro?.toString(),
    ProductEntityField.VITAMIN_E_MILLI to vitamins.vitaminEMilli?.toString(),
    ProductEntityField.VITAMIN_K_MICRO to vitamins.vitaminKMicro?.toString(),
    ProductEntityField.MANGANESE_MILLI to minerals.manganeseMilli?.toString(),
    ProductEntityField.MAGNESIUM_MILLI to minerals.magnesiumMilli?.toString(),
    ProductEntityField.POTASSIUM_MILLI to minerals.potassiumMilli?.toString(),
    ProductEntityField.CALCIUM_MILLI to minerals.calciumMilli?.toString(),
    ProductEntityField.COPPER_MILLI to minerals.copperMilli?.toString(),
    ProductEntityField.ZINC_MILLI to minerals.zincMilli?.toString(),
    ProductEntityField.SODIUM_MILLI to minerals.sodiumMilli?.toString(),
    ProductEntityField.IRON_MILLI to minerals.ironMilli?.toString(),
    ProductEntityField.PHOSPHORUS_MILLI to minerals.phosphorusMilli?.toString(),
    ProductEntityField.SELENIUM_MICRO to minerals.seleniumMicro?.toString(),
    ProductEntityField.IODINE_MICRO to minerals.iodineMicro?.toString(),
    ProductEntityField.PACKAGE_WEIGHT to packageWeight?.toString(),
    ProductEntityField.SERVING_WEIGHT to servingWeight?.toString()
)

private fun Map<ProductEntityField, String>.toProductEntity(): ProductEntity? {
    val proteins = this[ProductEntityField.PROTEINS]?.toFloatOrNull() ?: return null
    val carbohydrates = this[ProductEntityField.CARBOHYDRATES]?.toFloatOrNull() ?: return null
    val fats = this[ProductEntityField.FATS]?.toFloatOrNull() ?: return null
    val calories = this[ProductEntityField.CALORIES]?.toFloatOrNull() ?: return null

    val nutrients = Nutrients(
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        calories = calories,
        saturatedFats = this[ProductEntityField.SATURATED_FATS]?.toFloatOrNull(),
        monounsaturatedFats = this[ProductEntityField.MONOUNSATURATED_FATS]?.toFloatOrNull(),
        polyunsaturatedFats = this[ProductEntityField.POLYUNSATURATED_FATS]?.toFloatOrNull(),
        omega3 = this[ProductEntityField.OMEGA3]?.toFloatOrNull(),
        omega6 = this[ProductEntityField.OMEGA6]?.toFloatOrNull(),
        sugars = this[ProductEntityField.SUGARS]?.toFloatOrNull(),
        salt = this[ProductEntityField.SALT]?.toFloatOrNull(),
        fiber = this[ProductEntityField.FIBER]?.toFloatOrNull(),
        cholesterolMilli = this[ProductEntityField.CHOLESTEROL_MILLI]?.toFloatOrNull(),
        caffeineMilli = this[ProductEntityField.CAFFEINE_MILLI]?.toFloatOrNull()
    )

    val vitamins = Vitamins(
        vitaminAMicro = this[ProductEntityField.VITAMIN_A_MICRO]?.toFloatOrNull(),
        vitaminB1Milli = this[ProductEntityField.VITAMIN_B1_MILLI]?.toFloatOrNull(),
        vitaminB2Milli = this[ProductEntityField.VITAMIN_B2_MILLI]?.toFloatOrNull(),
        vitaminB3Milli = this[ProductEntityField.VITAMIN_B3_MILLI]?.toFloatOrNull(),
        vitaminB5Milli = this[ProductEntityField.VITAMIN_B5_MILLI]?.toFloatOrNull(),
        vitaminB6Milli = this[ProductEntityField.VITAMIN_B6_MILLI]?.toFloatOrNull(),
        vitaminB7Micro = this[ProductEntityField.VITAMIN_B7_MICRO]?.toFloatOrNull(),
        vitaminB9Micro = this[ProductEntityField.VITAMIN_B9_MICRO]?.toFloatOrNull(),
        vitaminB12Micro = this[ProductEntityField.VITAMIN_B12_MICRO]?.toFloatOrNull(),
        vitaminCMilli = this[ProductEntityField.VITAMIN_C_MILLI]?.toFloatOrNull(),
        vitaminDMicro = this[ProductEntityField.VITAMIN_D_MICRO]?.toFloatOrNull(),
        vitaminEMilli = this[ProductEntityField.VITAMIN_E_MILLI]?.toFloatOrNull(),
        vitaminKMicro = this[ProductEntityField.VITAMIN_K_MICRO]?.toFloatOrNull()
    )

    val minerals = Minerals(
        manganeseMilli = this[ProductEntityField.MANGANESE_MILLI]?.toFloatOrNull(),
        magnesiumMilli = this[ProductEntityField.MAGNESIUM_MILLI]?.toFloatOrNull(),
        potassiumMilli = this[ProductEntityField.POTASSIUM_MILLI]?.toFloatOrNull(),
        calciumMilli = this[ProductEntityField.CALCIUM_MILLI]?.toFloatOrNull(),
        copperMilli = this[ProductEntityField.COPPER_MILLI]?.toFloatOrNull(),
        zincMilli = this[ProductEntityField.ZINC_MILLI]?.toFloatOrNull(),
        sodiumMilli = this[ProductEntityField.SODIUM_MILLI]?.toFloatOrNull(),
        ironMilli = this[ProductEntityField.IRON_MILLI]?.toFloatOrNull(),
        phosphorusMilli = this[ProductEntityField.PHOSPHORUS_MILLI]?.toFloatOrNull(),
        seleniumMicro = this[ProductEntityField.SELENIUM_MICRO]?.toFloatOrNull(),
        iodineMicro = this[ProductEntityField.IODINE_MICRO]?.toFloatOrNull(),
        chromiumMicro = this[ProductEntityField.CHROMIUM_MICRO]?.toFloatOrNull()
    )

    val name = this[ProductEntityField.NAME]?.toString() ?: return null

    return ProductEntity(
        name = name,
        brand = this[ProductEntityField.BRAND],
        barcode = this[ProductEntityField.BARCODE],
        nutrients = nutrients,
        vitamins = vitamins,
        minerals = minerals,
        packageWeight = this[ProductEntityField.PACKAGE_WEIGHT]?.toFloatOrNull(),
        servingWeight = this[ProductEntityField.SERVING_WEIGHT]?.toFloatOrNull()
    )
}
