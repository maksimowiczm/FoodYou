package com.maksimowiczm.foodyou.userfood.infrastructure.product

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FluidOunces
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Ounces
import com.maksimowiczm.foodyou.common.infrastructure.food.NutrientsMapper
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBarcode
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBrand
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductSource
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.FoodNameEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.ProductEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.QuantityEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.QuantityType

internal class ProductMapper {
    private val nutrientsMapper = NutrientsMapper()

    fun userProduct(entity: ProductEntity): UserProduct =
        with(entity) {
            val name =
                FoodName.requireAll(
                    english = name.english,
                    catalan = name.catalan,
                    czech = name.czech,
                    danish = name.danish,
                    german = name.german,
                    spanish = name.spanish,
                    french = name.french,
                    indonesian = name.indonesian,
                    italian = name.italian,
                    hungarian = name.hungarian,
                    dutch = name.dutch,
                    polish = name.polish,
                    portugueseBrazil = name.portugueseBrazil,
                    slovenian = name.slovenian,
                    turkish = name.turkish,
                    russian = name.russian,
                    ukrainian = name.ukrainian,
                    arabic = name.arabic,
                    chineseSimplified = name.chineseSimplified,
                    fallback = name.fallback,
                )

            val nutrients = nutrientsMapper.toNutritionFats(nutrients)

            val servingQuantity = servingSize?.toQuantity()
            val packageQuantity = packageSize?.toQuantity()

            val brand = brand?.let { UserProductBrand(it) }

            UserProduct(
                identity = UserProductIdentity(entity.uuid, LocalAccountId(entity.accountId)),
                name = name,
                brand = brand,
                barcode = entity.barcode?.let { UserProductBarcode(it) },
                note = entity.note?.let { UserFoodNote(it) },
                image = entity.photoPath?.let { Image.Local(it) },
                source = entity.source?.let { UserProductSource(it) },
                nutritionFacts = nutrients,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )
        }

    fun toEntity(
        id: Long = 0,
        uuid: String,
        name: FoodName,
        brand: UserProductBrand?,
        barcode: UserProductBarcode?,
        note: UserFoodNote?,
        imagePath: String?,
        source: UserProductSource?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    ): ProductEntity {
        val servingSize = servingQuantity?.let { toQuantityEntity(it) }
        val packageSize = packageQuantity?.let { toQuantityEntity(it) }

        val foodName =
            FoodNameEntity(
                english = name.english,
                catalan = name.catalan,
                danish = name.danish,
                german = name.german,
                spanish = name.spanish,
                french = name.french,
                indonesian = name.indonesian,
                italian = name.italian,
                hungarian = name.hungarian,
                dutch = name.dutch,
                polish = name.polish,
                portugueseBrazil = name.portugueseBrazil,
                slovenian = name.slovenian,
                turkish = name.turkish,
                russian = name.russian,
                ukrainian = name.ukrainian,
                arabic = name.arabic,
                chineseSimplified = name.chineseSimplified,
            )

        return ProductEntity(
            sqliteId = id,
            uuid = uuid,
            name = foodName,
            brand = brand?.value,
            barcode = barcode?.value,
            note = note?.value,
            source = source?.value,
            photoPath = imagePath,
            accountId = accountId.value,
            nutrients = nutrientsMapper.toNutrientsEntity(nutritionFacts),
            packageSize = packageSize,
            servingSize = servingSize,
            isLiquid = isLiquid,
        )
    }

    fun toQuantityEntity(quantity: AbsoluteQuantity): QuantityEntity =
        when (quantity) {
            is AbsoluteQuantity.Weight -> {
                when (quantity.weight) {
                    is Grams ->
                        QuantityEntity(
                            type = QuantityType.Weight,
                            amount = quantity.weight.grams,
                            unit = MeasurementUnit.Grams,
                        )

                    is Ounces ->
                        QuantityEntity(
                            type = QuantityType.Weight,
                            amount = quantity.weight.ounces,
                            unit = MeasurementUnit.Ounces,
                        )
                }
            }

            is AbsoluteQuantity.Volume -> {
                when (quantity.volume) {
                    is Milliliters ->
                        QuantityEntity(
                            type = QuantityType.Volume,
                            amount = quantity.volume.milliliters,
                            unit = MeasurementUnit.Milliliters,
                        )

                    is FluidOunces ->
                        QuantityEntity(
                            type = QuantityType.Volume,
                            amount = quantity.volume.fluidOunces,
                            unit = MeasurementUnit.FluidOunces,
                        )
                }
            }
        }

    private fun QuantityEntity.toQuantity(): AbsoluteQuantity =
        when (type) {
            QuantityType.Weight -> {
                val weight =
                    when (unit) {
                        MeasurementUnit.Grams -> Grams(amount)
                        MeasurementUnit.Ounces -> Ounces(amount)
                        MeasurementUnit.Milliliters,
                        MeasurementUnit.FluidOunces -> error("Invalid unit for weight: $unit")
                    }
                AbsoluteQuantity.Weight(weight)
            }

            QuantityType.Volume -> {
                val volume =
                    when (unit) {
                        MeasurementUnit.Grams,
                        MeasurementUnit.Ounces -> error("Invalid unit for volume: $unit")

                        MeasurementUnit.Milliliters -> Milliliters(amount)
                        MeasurementUnit.FluidOunces -> FluidOunces(amount)
                    }
                AbsoluteQuantity.Volume(volume)
            }
        }
}
