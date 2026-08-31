package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsProduct(
    val identity: OpenFoodFactsProductIdentity,
    val name: FoodName,
    val brand: String?,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
    val thumbnail: FileUri?,
    val image: FileUri?,
    val source: String,
)

fun OpenFoodFactsProduct.toSnapshot() =
    LeafFoodSnapshot(
        id = FoodSnapshotId.OpenFoodFacts(identity.barcode),
        name = name,
        brand = brand,
        nutritionFacts = nutritionFacts,
        image = (thumbnail ?: image)?.let(FoodSnapshotImage::Uri),
    )
