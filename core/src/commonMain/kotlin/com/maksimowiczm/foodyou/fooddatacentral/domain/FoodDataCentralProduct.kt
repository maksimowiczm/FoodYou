package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * FoodData Central identifier from the USDA database.
 *
 * @property fdcId The FoodData Central unique identifier
 */
@Serializable @JvmInline value class FoodDataCentralProductId(val fdcId: Int)

@Serializable
data class FoodDataCentralProduct(
    val id: FoodDataCentralProductId,
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
        id = FoodSnapshotId.FoodDataCentral(id.fdcId),
        name = FoodName(fallback = name),
        brand = brand,
        image = null,
        nutritionFacts = nutritionFacts,
    )
