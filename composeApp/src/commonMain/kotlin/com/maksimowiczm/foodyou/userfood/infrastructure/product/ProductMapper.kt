package com.maksimowiczm.foodyou.userfood.infrastructure.product

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.domain.ounces
import com.maksimowiczm.foodyou.common.infrastructure.food.NutrientsMapper
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBarcode
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBrand
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
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

            val servingQuantity = servingSize?.toAbsoluteQuantity()
            val packageQuantity = packageSize?.toAbsoluteQuantity()

            val brand = brand?.let { UserProductBrand(it) }

            UserProduct(
                identity = UserProductIdentity(entity.uuid),
                name = name,
                brand = brand,
                barcode = entity.barcode?.let { UserProductBarcode(it) },
                note = entity.note?.let { UserFoodNote(it) },
                image = entity.photoPath?.let { Image.Local(it) },
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
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
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
            photoPath = imagePath,
            nutrients = nutrientsMapper.toNutrientsEntity(nutritionFacts),
            packageSize = packageSize,
            servingSize = servingSize,
            isLiquid = isLiquid,
        )
    }

    private fun toQuantityEntity(quantity: AbsoluteQuantity): QuantityEntity {
        return when (quantity) {
            is AbsoluteQuantity.Weight ->
                when (quantity.weight.unit) {
                    WeightUnit.Micrograms ->
                        QuantityEntity(
                            QuantityType.Weight,
                            quantity.weight.micrograms,
                            MeasurementUnit.Micrograms,
                        )

                    WeightUnit.Milligrams ->
                        QuantityEntity(
                            QuantityType.Weight,
                            quantity.weight.milligrams,
                            MeasurementUnit.Milligrams,
                        )

                    WeightUnit.Grams ->
                        QuantityEntity(
                            QuantityType.Weight,
                            quantity.weight.grams,
                            MeasurementUnit.Grams,
                        )

                    WeightUnit.Ounces ->
                        QuantityEntity(
                            QuantityType.Weight,
                            quantity.weight.ounces,
                            MeasurementUnit.Ounces,
                        )
                }

            is AbsoluteQuantity.Volume ->
                when (quantity.volume.unit) {
                    VolumeUnit.Milliliters ->
                        QuantityEntity(
                            QuantityType.Volume,
                            quantity.volume.milliliters,
                            MeasurementUnit.Milliliters,
                        )

                    VolumeUnit.FluidOunces ->
                        QuantityEntity(
                            QuantityType.Volume,
                            quantity.volume.fluidOunces,
                            MeasurementUnit.FluidOunces,
                        )
                }
        }
    }

    private fun QuantityEntity.toAbsoluteQuantity(): AbsoluteQuantity =
        when (type) {
            QuantityType.Weight ->
                AbsoluteQuantity.Weight(
                    weight =
                        when (unit) {
                            MeasurementUnit.Micrograms -> amount.micrograms
                            MeasurementUnit.Grams -> amount.grams
                            MeasurementUnit.Ounces -> amount.ounces
                            else -> error("Unexpected unit $unit for weight")
                        }
                )

            QuantityType.Volume ->
                AbsoluteQuantity.Volume(
                    volume =
                        when (unit) {
                            MeasurementUnit.Milliliters -> amount.milliliters
                            MeasurementUnit.FluidOunces -> amount.fluidOunces
                            else -> error("Unexpected unit $unit for volume")
                        }
                )
        }
}
