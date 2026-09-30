package com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodNameMapper
import com.maksimowiczm.foodyou.common.infrastructure.room.NutrientsMapper
import com.maksimowiczm.foodyou.common.infrastructure.room.QuantityMapper
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe

object SearchResultMapper {
    fun toDomain(entity: SearchEntity): SearchResult =
        with(entity) {
            check(productId != null || recipeId != null) {
                "Either productId or recipeId must be non-null"
            }

            if (recipeId != null) {
                return SearchResult.UserRecipe(
                    id = recipeId,
                    name = FoodNameMapper.toFoodName(name),
                    note = note,
                    image = imageDigest?.let(::BlobDigest),
                    nutritionFacts = NutrientsMapper.toNutritionFats(nutrients),
                    servingWeight =
                        servingSize
                            ?.let {
                                QuantityMapper.toAbsoluteQuantity(it) as? AbsoluteQuantity.Weight
                            }
                            ?.weight ?: 0.grams,
                    totalWeight =
                        packageSize
                            ?.let {
                                QuantityMapper.toAbsoluteQuantity(it) as? AbsoluteQuantity.Weight
                            }
                            ?.weight ?: 0.grams,
                )
            }

            checkNotNull(productId)

            val servingQuantity = servingSize?.let(QuantityMapper::toAbsoluteQuantity)
            val packageQuantity = packageSize?.let(QuantityMapper::toAbsoluteQuantity)

            return SearchResult.UserProduct(
                id = productId,
                name = FoodNameMapper.toFoodName(name),
                brand = brand,
                barcode = barcode?.let(::UserProductBarcode),
                note = note,
                image = imageDigest?.let(::BlobDigest),
                nutritionFacts = NutrientsMapper.toNutritionFats(nutrients),
                packageQuantity = packageQuantity,
                servingQuantity = servingQuantity,
                isLiquid = isLiquid,
            )
        }

    fun toSearchEntity(userProduct: UserProduct): SearchEntity =
        with(userProduct) {
            val servingSize = servingQuantity?.let { QuantityMapper.toQuantityEntity(it) }
            val packageSize = packageQuantity?.let { QuantityMapper.toQuantityEntity(it) }

            return SearchEntity(
                productId = id.value,
                recipeId = null,
                name = FoodNameMapper.toFoodNameEntity(name),
                brand = brand,
                barcode = barcode?.value,
                note = note,
                imageDigest = image?.digest,
                nutrients = NutrientsMapper.toNutrientsEntity(nutritionFacts),
                packageSize = packageSize,
                servingSize = servingSize,
                isLiquid = isLiquid,
                servings = null,
            )
        }

    fun toSearchEntity(userRecipe: UserRecipe) =
        with(userRecipe) {
            SearchEntity(
                productId = null,
                recipeId = id.value,
                name = FoodNameMapper.toFoodNameEntity(name),
                brand = null,
                barcode = null,
                note = note,
                imageDigest = image?.digest,
                nutrients = NutrientsMapper.toNutrientsEntity(nutritionFacts),
                packageSize = QuantityMapper.toQuantityEntity(AbsoluteQuantity.Weight(totalWeight)),
                servingSize =
                    QuantityMapper.toQuantityEntity(AbsoluteQuantity.Weight(servingWeight)),
                isLiquid = false,
                servings = null,
            )
        }
}
