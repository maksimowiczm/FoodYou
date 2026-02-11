package com.maksimowiczm.foodyou.userfood.infrastructure.product

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Barcode
import com.maksimowiczm.foodyou.common.domain.food.FluidOunces
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Ounces
import com.maksimowiczm.foodyou.common.infrastructure.food.NutrientsMapper
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit
import com.maksimowiczm.foodyou.userfood.domain.FoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.FoodBrand
import com.maksimowiczm.foodyou.userfood.domain.product.FoodSource
import com.maksimowiczm.foodyou.userfood.domain.product.UserFoodProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserFoodProductIdentity
import com.maksimowiczm.foodyou.userfood.infrastructure.product.room.FoodNameEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.product.room.QuantityEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.product.room.QuantityType
import com.maksimowiczm.foodyou.userfood.infrastructure.product.room.UserFoodEntity

internal class UserFoodMapper {
    private val nutrientsMapper = NutrientsMapper()

    fun userFoodProduct(entity: UserFoodEntity): UserFoodProduct =
        buildFoodDto(entity) { name, brand, nutrients, servingQuantity, packageQuantity, isLiquid ->
            UserFoodProduct(
                identity = UserFoodProductIdentity(entity.uuid, LocalAccountId(entity.accountId)),
                name = name,
                brand = brand,
                barcode = entity.barcode?.let { Barcode(it) },
                note = entity.note?.let { FoodNote(it) },
                image = entity.photoPath?.let { Image.Local(it) },
                source = entity.source?.let { FoodSource(it) },
                nutritionFacts = nutrients,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )
        }

    private inline fun <T> buildFoodDto(
        entity: UserFoodEntity,
        build:
            (
                FoodName,
                FoodBrand?,
                NutritionFacts,
                AbsoluteQuantity?,
                AbsoluteQuantity?,
                isLiquid: Boolean,
            ) -> T,
    ): T =
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

            val brand = brand?.let { FoodBrand(it) }

            return build(name, brand, nutrients, servingQuantity, packageQuantity, isLiquid)
        }

    fun toEntity(
        id: Long = 0,
        uuid: String,
        name: FoodName,
        brand: FoodBrand?,
        barcode: Barcode?,
        note: FoodNote?,
        imagePath: String?,
        source: FoodSource?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    ): UserFoodEntity {
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

        return UserFoodEntity(
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
