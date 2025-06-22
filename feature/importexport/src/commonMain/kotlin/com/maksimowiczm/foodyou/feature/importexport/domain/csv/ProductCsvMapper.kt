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

private fun ProductEntity.toStringMap(): Map<ProductEntityField, String?> =
    ProductEntityField.entries.associateWith {
        when (it) {
            ProductEntityField.NAME -> name.toString()
            ProductEntityField.BRAND -> brand?.toString()
            ProductEntityField.BARCODE -> barcode?.toString()
            ProductEntityField.PROTEINS -> nutrients.proteins.toString()
            ProductEntityField.CARBOHYDRATES -> nutrients.carbohydrates.toString()
            ProductEntityField.FATS -> nutrients.fats.toString()
            ProductEntityField.CALORIES -> nutrients.calories.toString()
            ProductEntityField.SATURATED_FATS -> nutrients.saturatedFats?.toString()
            ProductEntityField.MONOUNSATURATED_FATS -> nutrients.monounsaturatedFats?.toString()
            ProductEntityField.POLYUNSATURATED_FATS -> nutrients.polyunsaturatedFats?.toString()
            ProductEntityField.OMEGA3 -> nutrients.omega3?.toString()
            ProductEntityField.OMEGA6 -> nutrients.omega6?.toString()
            ProductEntityField.SUGARS -> nutrients.sugars?.toString()
            ProductEntityField.SALT -> nutrients.salt?.toString()
            ProductEntityField.FIBER -> nutrients.fiber?.toString()
            ProductEntityField.CHOLESTEROL_MILLI -> nutrients.cholesterolMilli?.toString()
            ProductEntityField.CAFFEINE_MILLI -> nutrients.caffeineMilli?.toString()
            ProductEntityField.VITAMIN_A_MICRO -> vitamins.vitaminAMicro?.toString()
            ProductEntityField.VITAMIN_B1_MILLI -> vitamins.vitaminB1Milli?.toString()
            ProductEntityField.VITAMIN_B2_MILLI -> vitamins.vitaminB2Milli?.toString()
            ProductEntityField.VITAMIN_B3_MILLI -> vitamins.vitaminB3Milli?.toString()
            ProductEntityField.VITAMIN_B5_MILLI -> vitamins.vitaminB5Milli?.toString()
            ProductEntityField.VITAMIN_B6_MILLI -> vitamins.vitaminB6Milli?.toString()
            ProductEntityField.VITAMIN_B7_MICRO -> vitamins.vitaminB7Micro?.toString()
            ProductEntityField.VITAMIN_B9_MICRO -> vitamins.vitaminB9Micro?.toString()
            ProductEntityField.VITAMIN_B12_MICRO -> vitamins.vitaminB12Micro?.toString()
            ProductEntityField.VITAMIN_C_MILLI -> vitamins.vitaminCMilli?.toString()
            ProductEntityField.VITAMIN_D_MICRO -> vitamins.vitaminDMicro?.toString()
            ProductEntityField.VITAMIN_E_MILLI -> vitamins.vitaminEMilli?.toString()
            ProductEntityField.VITAMIN_K_MICRO -> vitamins.vitaminKMicro?.toString()
            ProductEntityField.MANGANESE_MILLI -> minerals.manganeseMilli?.toString()
            ProductEntityField.MAGNESIUM_MILLI -> minerals.magnesiumMilli?.toString()
            ProductEntityField.POTASSIUM_MILLI -> minerals.potassiumMilli?.toString()
            ProductEntityField.CALCIUM_MILLI -> minerals.calciumMilli?.toString()
            ProductEntityField.COPPER_MILLI -> minerals.copperMilli?.toString()
            ProductEntityField.ZINC_MILLI -> minerals.zincMilli?.toString()
            ProductEntityField.SODIUM_MILLI -> minerals.sodiumMilli?.toString()
            ProductEntityField.IRON_MILLI -> minerals.ironMilli?.toString()
            ProductEntityField.PHOSPHORUS_MILLI -> minerals.phosphorusMilli?.toString()
            ProductEntityField.SELENIUM_MICRO -> minerals.seleniumMicro?.toString()
            ProductEntityField.IODINE_MICRO -> minerals.iodineMicro?.toString()
            ProductEntityField.CHROMIUM_MICRO -> minerals.chromiumMicro?.toString()
            ProductEntityField.PACKAGE_WEIGHT -> packageWeight?.toString()
            ProductEntityField.SERVING_WEIGHT -> servingWeight?.toString()
            ProductEntityField.IS_LIQUID -> isLiquid.toString()
        }
    }

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
        servingWeight = this[ProductEntityField.SERVING_WEIGHT]?.toFloatOrNull(),
        isLiquid = this[ProductEntityField.IS_LIQUID]?.toBooleanStrictOrNull() ?: false,
        note = null
    )
}
