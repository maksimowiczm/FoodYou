package com.maksimowiczm.foodyou.feature.importexport.domain

import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.Vitamins
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntityField
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource

internal object ProductCsvMapper {
    fun toStringMap(product: ProductEntity): Map<ProductEntityField, String?> =
        product.toStringMap()

    fun toProductEntity(map: Map<String, String>): ProductEntity? = map.toProductEntity()
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

private fun Map<String, String>.toProductEntity(): ProductEntity? {
    return ProductEntity(
        name = this[ProductEntityField.NAME.name].orEmpty(),
        brand = this[ProductEntityField.BRAND.name],
        barcode = this[ProductEntityField.BARCODE.name],
        nutrients = Nutrients(
            proteins = this[ProductEntityField.PROTEINS.name]?.toFloatOrNull() ?: return null,
            carbohydrates =
            this[ProductEntityField.CARBOHYDRATES.name]?.toFloatOrNull() ?: return null,
            fats = this[ProductEntityField.FATS.name]?.toFloatOrNull() ?: return null,
            calories = this[ProductEntityField.CALORIES.name]?.toFloatOrNull() ?: return null,
            saturatedFats = this[ProductEntityField.SATURATED_FATS.name]?.toFloatOrNull(),
            monounsaturatedFats =
            this[ProductEntityField.MONOUNSATURATED_FATS.name]?.toFloatOrNull(),
            polyunsaturatedFats =
            this[ProductEntityField.POLYUNSATURATED_FATS.name]?.toFloatOrNull(),
            omega3 = this[ProductEntityField.OMEGA3.name]?.toFloatOrNull(),
            omega6 = this[ProductEntityField.OMEGA6.name]?.toFloatOrNull(),
            sugars = this[ProductEntityField.SUGARS.name]?.toFloatOrNull(),
            salt = this[ProductEntityField.SALT.name]?.toFloatOrNull(),
            fiber = this[ProductEntityField.FIBER.name]?.toFloatOrNull(),
            cholesterolMilli = this[ProductEntityField.CHOLESTEROL_MILLI.name]?.toFloatOrNull(),
            caffeineMilli = this[ProductEntityField.CAFFEINE_MILLI.name]?.toFloatOrNull()
        ),
        vitamins = Vitamins(
            vitaminAMicro = this[ProductEntityField.VITAMIN_A_MICRO.name]?.toFloatOrNull(),
            vitaminB1Milli = this[ProductEntityField.VITAMIN_B1_MILLI.name]?.toFloatOrNull(),
            vitaminB2Milli = this[ProductEntityField.VITAMIN_B2_MILLI.name]?.toFloatOrNull(),
            vitaminB3Milli = this[ProductEntityField.VITAMIN_B3_MILLI.name]?.toFloatOrNull(),
            vitaminB5Milli = this[ProductEntityField.VITAMIN_B5_MILLI.name]?.toFloatOrNull(),
            vitaminB6Milli = this[ProductEntityField.VITAMIN_B6_MILLI.name]?.toFloatOrNull(),
            vitaminB7Micro = this[ProductEntityField.VITAMIN_B7_MICRO.name]?.toFloatOrNull(),
            vitaminB9Micro = this[ProductEntityField.VITAMIN_B9_MICRO.name]?.toFloatOrNull(),
            vitaminB12Micro = this[ProductEntityField.VITAMIN_B12_MICRO.name]?.toFloatOrNull(),
            vitaminCMilli = this[ProductEntityField.VITAMIN_C_MILLI.name]?.toFloatOrNull(),
            vitaminDMicro = this[ProductEntityField.VITAMIN_D_MICRO.name]?.toFloatOrNull(),
            vitaminEMilli = this[ProductEntityField.VITAMIN_E_MILLI.name]?.toFloatOrNull(),
            vitaminKMicro = this[ProductEntityField.VITAMIN_K_MICRO.name]?.toFloatOrNull()
        ),
        minerals = com.maksimowiczm.foodyou.core.data.model.Minerals(
            manganeseMilli = this[ProductEntityField.MANGANESE_MILLI.name]?.toFloatOrNull(),
            magnesiumMilli = this[ProductEntityField.MAGNESIUM_MILLI.name]?.toFloatOrNull(),
            potassiumMilli = this[ProductEntityField.POTASSIUM_MILLI.name]?.toFloatOrNull(),
            calciumMilli = this[ProductEntityField.CALCIUM_MILLI.name]?.toFloatOrNull(),
            copperMilli = this[ProductEntityField.COPPER_MILLI.name]?.toFloatOrNull(),
            zincMilli = this[ProductEntityField.ZINC_MILLI.name]?.toFloatOrNull(),
            sodiumMilli = this[ProductEntityField.SODIUM_MILLI.name]?.toFloatOrNull(),
            ironMilli = this[ProductEntityField.IRON_MILLI.name]?.toFloatOrNull(),
            phosphorusMilli = this[ProductEntityField.PHOSPHORUS_MILLI.name]?.toFloatOrNull(),
            seleniumMicro = this[ProductEntityField.SELENIUM_MICRO.name]?.toFloatOrNull(),
            iodineMicro = this[ProductEntityField.IODINE_MICRO.name]?.toFloatOrNull()
        ),
        packageWeight = this[ProductEntityField.PACKAGE_WEIGHT.name]?.toFloatOrNull(),
        servingWeight = this[ProductEntityField.SERVING_WEIGHT.name]?.toFloatOrNull(),
        productSource = ProductSource.User
    )
}
