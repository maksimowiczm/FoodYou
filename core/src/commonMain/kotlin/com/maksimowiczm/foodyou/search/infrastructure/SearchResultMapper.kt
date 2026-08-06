package com.maksimowiczm.foodyou.search.infrastructure

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.domain.ounces
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit
import com.maksimowiczm.foodyou.common.infrastructure.room.NutrientsMapper
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode

class SearchResultMapper {
    private val nutrientsMapper = NutrientsMapper()

    fun toDomain(entity: SearchEntity): SearchResult =
        with(entity) {
            check(productId != null || recipeId != null) {
                "Either productId or recipeId must be non-null"
            }

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
                    portuguesePortugal = name.portuguesePortugal,
                    slovenian = name.slovenian,
                    turkish = name.turkish,
                    russian = name.russian,
                    ukrainian = name.ukrainian,
                    arabic = name.arabic,
                    chineseSimplified = name.chineseSimplified,
                    fallback = name.fallback,
                )

            val nutritionFacts = nutrientsMapper.toNutritionFats(nutrients)

            if (recipeId != null) {
                return SearchResult.UserRecipe(
                    id = recipeId,
                    name = name,
                    note = note,
                    image = imageDigest?.let(::BlobDigest),
                    nutritionFacts = nutritionFacts,
                    servingWeight =
                        servingSize?.toAbsoluteQuantity()?.let {
                            (it as? AbsoluteQuantity.Weight)?.weight
                        } ?: 0.grams,
                    totalWeight =
                        packageSize?.toAbsoluteQuantity()?.let {
                            (it as? AbsoluteQuantity.Weight)?.weight
                        } ?: 0.grams,
                )
            }

            checkNotNull(productId)

            val servingQuantity = servingSize?.toAbsoluteQuantity()
            val packageQuantity = packageSize?.toAbsoluteQuantity()

            return SearchResult.UserProduct(
                id = productId,
                name = name,
                brand = brand,
                barcode = barcode?.let(::UserProductBarcode),
                note = note,
                image = imageDigest?.let(::BlobDigest),
                nutritionFacts = nutritionFacts,
                packageQuantity = packageQuantity,
                servingQuantity = servingQuantity,
                isLiquid = isLiquid,
            )
        }

    fun toEntity(domain: SearchResult): SearchEntity =
        when (domain) {
            is SearchResult.UserProduct -> toEntity(domain)
            is SearchResult.UserRecipe -> toEntity(domain)
        }

    fun toEntity(domain: SearchResult.UserProduct): SearchEntity =
        with(domain) {
            val servingSize = servingQuantity?.let { toQuantityEntity(it) }
            val packageSize = packageQuantity?.let { toQuantityEntity(it) }

            val foodName = toFoodNameEntity(name)

            return SearchEntity(
                productId = id,
                recipeId = null,
                name = foodName,
                brand = brand,
                barcode = barcode?.value,
                note = note,
                imageDigest = image?.digest,
                nutrients = nutrientsMapper.toNutrientsEntity(nutritionFacts),
                packageSize = packageSize,
                servingSize = servingSize,
                isLiquid = isLiquid,
                servings = null,
            )
        }

    fun toEntity(domain: SearchResult.UserRecipe): SearchEntity =
        with(domain) {
            val foodName = toFoodNameEntity(name)

            return SearchEntity(
                productId = null,
                recipeId = id,
                name = foodName,
                brand = null,
                barcode = null,
                note = note,
                imageDigest = image?.digest,
                nutrients = nutrientsMapper.toNutrientsEntity(nutritionFacts),
                packageSize = toQuantityEntity(AbsoluteQuantity.Weight(totalWeight)),
                servingSize = toQuantityEntity(AbsoluteQuantity.Weight(servingWeight)),
                isLiquid = false,
                servings = null,
            )
        }

    private fun toFoodNameEntity(name: FoodName) =
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
            portuguesePortugal = name.portuguesePortugal,
            slovenian = name.slovenian,
            turkish = name.turkish,
            russian = name.russian,
            ukrainian = name.ukrainian,
            arabic = name.arabic,
            chineseSimplified = name.chineseSimplified,
        )

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
