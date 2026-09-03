package com.maksimowiczm.foodyou.openfoodfacts.domain

import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * Open Food Facts identifier using product barcode.
 *
 * @property barcode The product barcode
 */
@Serializable @JvmInline value class OpenFoodFactsProductId(val barcode: String)

@Serializable
data class OpenFoodFactsProduct(
    val id: OpenFoodFactsProductId,
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
        id = FoodSnapshotId.OpenFoodFacts(id.barcode),
        name = name,
        brand = brand,
        nutritionFacts = nutritionFacts,
        image = (thumbnail ?: image)?.let(FoodSnapshotImage::Uri),
    )

fun FoodSnapshotId.OpenFoodFacts.toOpenFoodFactsProductId() = OpenFoodFactsProductId(barcode)
