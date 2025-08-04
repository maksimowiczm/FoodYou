package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.food.Minerals
import com.maksimowiczm.foodyou.feature.food.data.database.food.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product
import com.maksimowiczm.foodyou.feature.food.data.database.food.Vitamins

internal interface RemoteProductMapper {

    /**
     * Maps a [RemoteProduct] to a [Product] entity.
     *
     * @param remoteProduct The remote product to map.
     * @throws IllegalArgumentException if the product name is null.
     */
    fun toEntity(remoteProduct: RemoteProduct, note: String? = null): Product
}

internal class RemoteProductMapperImpl : RemoteProductMapper {

    override fun toEntity(remoteProduct: RemoteProduct, note: String?): Product =
        with(remoteProduct) {
            if (name == null) {
                error("Product name cannot be null")
            }

            return Product(
                name = name,
                brand = brand?.takeIf { it.isNotBlank() },
                barcode = barcode?.takeIf { it.isNotBlank() },
                packageWeight = packageWeight,
                servingWeight = servingWeight,
                nutrients = Nutrients(
                    energy = nutritionFacts?.energy,
                    proteins = nutritionFacts?.proteins,
                    fats = nutritionFacts?.fats,
                    saturatedFats = nutritionFacts?.saturatedFats,
                    transFats = nutritionFacts?.transFats,
                    monounsaturatedFats = nutritionFacts?.monounsaturatedFats,
                    polyunsaturatedFats = nutritionFacts?.polyunsaturatedFats,
                    omega3 = nutritionFacts?.omega3,
                    omega6 = nutritionFacts?.omega6,
                    carbohydrates = nutritionFacts?.carbohydrates,
                    sugars = nutritionFacts?.sugars,
                    addedSugars = nutritionFacts?.addedSugars,
                    dietaryFiber = nutritionFacts?.dietaryFiber,
                    solubleFiber = nutritionFacts?.solubleFiber,
                    insolubleFiber = nutritionFacts?.insolubleFiber,
                    salt = nutritionFacts?.salt,
                    cholesterolMilli = nutritionFacts?.cholesterolMilli,
                    caffeineMilli = nutritionFacts?.caffeineMilli
                ),
                vitamins = Vitamins(
                    vitaminAMicro = nutritionFacts?.vitaminAMicro,
                    vitaminB1Milli = nutritionFacts?.vitaminB1Milli,
                    vitaminB2Milli = nutritionFacts?.vitaminB2Milli,
                    vitaminB3Milli = nutritionFacts?.vitaminB3Milli,
                    vitaminB5Milli = nutritionFacts?.vitaminB5Milli,
                    vitaminB6Milli = nutritionFacts?.vitaminB6Milli,
                    vitaminB7Micro = nutritionFacts?.vitaminB7Micro,
                    vitaminB9Micro = nutritionFacts?.vitaminB9Micro,
                    vitaminB12Micro = nutritionFacts?.vitaminB12Micro,
                    vitaminCMilli = nutritionFacts?.vitaminCMilli,
                    vitaminDMicro = nutritionFacts?.vitaminDMicro,
                    vitaminEMilli = nutritionFacts?.vitaminEMilli,
                    vitaminKMicro = nutritionFacts?.vitaminKMicro
                ),
                minerals = Minerals(
                    manganeseMilli = nutritionFacts?.manganeseMilli,
                    magnesiumMilli = nutritionFacts?.magnesiumMilli,
                    potassiumMilli = nutritionFacts?.potassiumMilli,
                    calciumMilli = nutritionFacts?.calciumMilli,
                    copperMilli = nutritionFacts?.copperMilli,
                    zincMilli = nutritionFacts?.zincMilli,
                    sodiumMilli = nutritionFacts?.sodiumMilli,
                    ironMilli = nutritionFacts?.ironMilli,
                    phosphorusMilli = nutritionFacts?.phosphorusMilli,
                    seleniumMicro = nutritionFacts?.seleniumMicro,
                    iodineMicro = nutritionFacts?.iodineMicro,
                    chromiumMicro = nutritionFacts?.chromiumMicro
                ),
                note = note,
                sourceType = source.type,
                sourceUrl = source.url,
                isLiquid = isLiquid
            )
        }
}
