package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import kotlinx.serialization.Serializable

@Serializable
data class FoodDataCentralProduct(
    val identity: FoodDataCentralProductIdentity,
    val name: String,
    val brand: String?,
    val barcode: String?,
    val source: String,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
) {
    val headline: String = buildString {
        append(name)
        if (brand != null) {
            append(brand.let { " ($it)" })
        }
    }
}

fun FoodDataCentralProduct.toSnapshot() =
    LeafFoodSnapshot(
        id = FoodSnapshotId.FoodDataCentral(identity.fdcId),
        name = FoodName(fallback = name),
        brand = brand,
        image = null,
        nutritionFacts = nutritionFacts,
    )
