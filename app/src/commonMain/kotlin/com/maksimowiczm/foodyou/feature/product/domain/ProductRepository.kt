package com.maksimowiczm.foodyou.feature.product.domain

import com.maksimowiczm.foodyou.core.database.food.Minerals
import com.maksimowiczm.foodyou.core.database.food.Nutrients
import com.maksimowiczm.foodyou.core.database.food.ProductEntity
import com.maksimowiczm.foodyou.core.database.food.Vitamins
import com.maksimowiczm.foodyou.core.domain.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.model.Product

internal class ProductRepository(
//    private val localProductDataSource: ProductLocalDataSource
) {

    suspend fun getProductById(id: Long): Product? = with(ProductMapper) {
        TODO()
//        return localProductDataSource.observeProduct(id).first()?.toModel()
    }

    /**
     * Creates a new user product in the database.
     *
     * @return The ID of the newly created product.
     */
    suspend fun createProduct(
        name: String,
        brand: String?,
        barcode: String?,
        proteins: Float,
        carbohydrates: Float,
        fats: Float,
        calories: Float,
        saturatedFats: Float?,
        monounsaturatedFats: Float?,
        polyunsaturatedFats: Float?,
        omega3: Float?,
        omega6: Float?,
        sugars: Float?,
        salt: Float?,
        fiber: Float?,
        cholesterolMilli: Float?,
        caffeineMilli: Float?,
        vitaminAMicro: Float?,
        vitaminB1Milli: Float?,
        vitaminB2Milli: Float?,
        vitaminB3Milli: Float?,
        vitaminB5Milli: Float?,
        vitaminB6Milli: Float?,
        vitaminB7Micro: Float?,
        vitaminB9Micro: Float?,
        vitaminB12Micro: Float?,
        vitaminCMilli: Float?,
        vitaminDMicro: Float?,
        vitaminEMilli: Float?,
        vitaminKMicro: Float?,
        manganeseMilli: Float?,
        magnesiumMilli: Float?,
        potassiumMilli: Float?,
        calciumMilli: Float?,
        copperMilli: Float?,
        zincMilli: Float?,
        sodiumMilli: Float?,
        ironMilli: Float?,
        phosphorusMilli: Float?,
        seleniumMicro: Float?,
        iodineMicro: Float?,
        packageWeight: Float?,
        servingWeight: Float?
    ): Long {
        val nutrients = Nutrients(
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            calories = calories,
            saturatedFats = saturatedFats,
            monounsaturatedFats = monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats,
            omega3 = omega3,
            omega6 = omega6,
            sugars = sugars,
            salt = salt,
            fiber = fiber,
            cholesterolMilli = cholesterolMilli,
            caffeineMilli = caffeineMilli
        )

        val vitamins = Vitamins(
            vitaminAMicro = vitaminAMicro,
            vitaminB1Milli = vitaminB1Milli,
            vitaminB2Milli = vitaminB2Milli,
            vitaminB3Milli = vitaminB3Milli,
            vitaminB5Milli = vitaminB5Milli,
            vitaminB6Milli = vitaminB6Milli,
            vitaminB7Micro = vitaminB7Micro,
            vitaminB9Micro = vitaminB9Micro,
            vitaminB12Micro = vitaminB12Micro,
            vitaminCMilli = vitaminCMilli,
            vitaminDMicro = vitaminDMicro,
            vitaminEMilli = vitaminEMilli,
            vitaminKMicro = vitaminKMicro
        )

        val minerals = Minerals(
            manganeseMilli = manganeseMilli,
            magnesiumMilli = magnesiumMilli,
            potassiumMilli = potassiumMilli,
            calciumMilli = calciumMilli,
            copperMilli = copperMilli,
            zincMilli = zincMilli,
            sodiumMilli = sodiumMilli,
            ironMilli = ironMilli,
            phosphorusMilli = phosphorusMilli,
            seleniumMicro = seleniumMicro,
            iodineMicro = iodineMicro
        )

        val entity = ProductEntity(
            name = name,
            brand = brand?.takeIf { it.isNotBlank() },
            barcode = barcode?.takeIf { it.isNotBlank() },
            nutrients = nutrients,
            vitamins = vitamins,
            minerals = minerals,
            packageWeight = packageWeight,
            servingWeight = servingWeight
        )

//        return localProductDataSource.insertProduct(entity)
        TODO()
    }

    suspend fun updateProduct(
        id: Long,
        name: String,
        brand: String?,
        barcode: String?,
        proteins: Float,
        carbohydrates: Float,
        fats: Float,
        calories: Float,
        saturatedFats: Float?,
        monounsaturatedFats: Float?,
        polyunsaturatedFats: Float?,
        omega3: Float?,
        omega6: Float?,
        sugars: Float?,
        salt: Float?,
        fiber: Float?,
        cholesterolMilli: Float?,
        caffeineMilli: Float?,
        vitaminAMicro: Float?,
        vitaminB1Milli: Float?,
        vitaminB2Milli: Float?,
        vitaminB3Milli: Float?,
        vitaminB5Milli: Float?,
        vitaminB6Milli: Float?,
        vitaminB7Micro: Float?,
        vitaminB9Micro: Float?,
        vitaminB12Micro: Float?,
        vitaminCMilli: Float?,
        vitaminDMicro: Float?,
        vitaminEMilli: Float?,
        vitaminKMicro: Float?,
        manganeseMilli: Float?,
        magnesiumMilli: Float?,
        potassiumMilli: Float?,
        calciumMilli: Float?,
        copperMilli: Float?,
        zincMilli: Float?,
        sodiumMilli: Float?,
        ironMilli: Float?,
        phosphorusMilli: Float?,
        seleniumMicro: Float?,
        iodineMicro: Float?,
        packageWeight: Float?,
        servingWeight: Float?
    ) {
        val nutrients = Nutrients(
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            calories = calories,
            saturatedFats = saturatedFats,
            monounsaturatedFats = monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats,
            omega3 = omega3,
            omega6 = omega6,
            sugars = sugars,
            salt = salt,
            fiber = fiber,
            cholesterolMilli = cholesterolMilli,
            caffeineMilli = caffeineMilli
        )

        val vitamins = Vitamins(
            vitaminAMicro = vitaminAMicro,
            vitaminB1Milli = vitaminB1Milli,
            vitaminB2Milli = vitaminB2Milli,
            vitaminB3Milli = vitaminB3Milli,
            vitaminB5Milli = vitaminB5Milli,
            vitaminB6Milli = vitaminB6Milli,
            vitaminB7Micro = vitaminB7Micro,
            vitaminB9Micro = vitaminB9Micro,
            vitaminB12Micro = vitaminB12Micro,
            vitaminCMilli = vitaminCMilli,
            vitaminDMicro = vitaminDMicro,
            vitaminEMilli = vitaminEMilli,
            vitaminKMicro = vitaminKMicro
        )

        val minerals = Minerals(
            manganeseMilli = manganeseMilli,
            magnesiumMilli = magnesiumMilli,
            potassiumMilli = potassiumMilli,
            calciumMilli = calciumMilli,
            copperMilli = copperMilli,
            zincMilli = zincMilli,
            sodiumMilli = sodiumMilli,
            ironMilli = ironMilli,
            phosphorusMilli = phosphorusMilli,
            seleniumMicro = seleniumMicro,
            iodineMicro = iodineMicro
        )

        val entity = ProductEntity(
            id = id,
            name = name,
            brand = brand?.takeIf { it.isNotBlank() },
            barcode = barcode?.takeIf { it.isNotBlank() },
            nutrients = nutrients,
            vitamins = vitamins,
            minerals = minerals,
            packageWeight = packageWeight,
            servingWeight = servingWeight
        )

//        localProductDataSource.updateProduct(entity)
        TODO()
    }
}
