package com.maksimowiczm.foodyou.food.infrastructure.user

import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.FluidOunces
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.Milliliters
import com.maksimowiczm.foodyou.common.domain.Ounces
import com.maksimowiczm.foodyou.common.domain.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.ServingQuantity
import com.maksimowiczm.foodyou.food.domain.Barcode
import com.maksimowiczm.foodyou.food.domain.FoodBrand
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNote
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodSource
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.infrastructure.common.NutrientsMapper
import com.maksimowiczm.foodyou.food.infrastructure.user.room.FoodNameEntity
import com.maksimowiczm.foodyou.food.infrastructure.user.room.MeasurementUnit
import com.maksimowiczm.foodyou.food.infrastructure.user.room.QuantityEntity
import com.maksimowiczm.foodyou.food.infrastructure.user.room.QuantityType
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodEntity
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto

class UserFoodMapper {
    private val nutrientsMapper = NutrientsMapper()

    fun foodProductDto(entity: UserFoodEntity): FoodProductDto =
        buildFoodDto(entity) { name, brand, nutrients, servingQuantity, packageQuantity ->
            FoodProductDto(
                identity = FoodProductIdentity.Local(entity.id),
                name = name,
                brand = brand,
                barcode = entity.barcode?.let { Barcode(it) },
                note = entity.note?.let { FoodNote(it) },
                image = entity.photoPath?.let { FoodImage.Local(it) },
                source = entity.source?.let { FoodSource.UserAdded(it) },
                nutritionFacts = nutrients,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
            )
        }

    fun searchableFoodDto(entity: UserFoodEntity): SearchableFoodDto =
        buildFoodDto(entity) { name, brand, nutrients, servingQuantity, packageQuantity ->
            val suggestedQuantity =
                servingQuantity?.let { ServingQuantity(1.0) }
                    ?: packageQuantity?.let { PackageQuantity(1.0) }
                    ?: AbsoluteQuantity.Weight(Grams(100.0))

            val image = entity.photoPath?.let { FoodImage.Local(it) }

            SearchableFoodDto(
                identity = FoodProductIdentity.Local(entity.id),
                name = name,
                brand = brand,
                nutritionFacts = nutrients,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                suggestedQuantity = suggestedQuantity,
                image = image,
            )
        }

    private inline fun <T> buildFoodDto(
        entity: UserFoodEntity,
        build: (FoodName, FoodBrand?, NutritionFacts, AbsoluteQuantity?, AbsoluteQuantity?) -> T,
    ): T =
        with(entity) {
            val name =
                FoodName(
                    english = name.english,
                    catalan = name.catalan,
                    danish = name.danish,
                    german = name.german,
                    spanish = name.spanish,
                    french = name.french,
                    italian = name.italian,
                    hungarian = name.hungarian,
                    dutch = name.dutch,
                    polish = name.polish,
                    portugueseBrazil = name.portugueseBrazil,
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

            return build(name, brand, nutrients, servingQuantity, packageQuantity)
        }

    fun toEntity(
        id: String,
        name: FoodName,
        brand: FoodBrand?,
        barcode: Barcode?,
        note: FoodNote?,
        imagePath: String?,
        source: FoodSource.UserAdded?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
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
                italian = name.italian,
                hungarian = name.hungarian,
                dutch = name.dutch,
                polish = name.polish,
                portugueseBrazil = name.portugueseBrazil,
                turkish = name.turkish,
                russian = name.russian,
                ukrainian = name.ukrainian,
                arabic = name.arabic,
                chineseSimplified = name.chineseSimplified,
            )

        return UserFoodEntity(
            id = id,
            name = foodName,
            brand = brand?.value,
            barcode = barcode?.value,
            note = note?.value,
            photoPath = imagePath,
            source = source?.value,
            nutrients = nutrientsMapper.toNutrientsEntity(nutritionFacts),
            packageSize = packageSize,
            servingSize = servingSize,
            accountId = accountId.value,
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
