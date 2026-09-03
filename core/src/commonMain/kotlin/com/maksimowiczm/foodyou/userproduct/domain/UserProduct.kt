@file:MustUseReturnValues

package com.maksimowiczm.foodyou.userproduct.domain

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import kotlin.jvm.JvmInline
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable data class UserProductId(val value: Uuid = Uuid.random())

@Serializable
data class UserProduct(
    val id: UserProductId,
    val name: FoodName,
    val brand: String?,
    val barcode: UserProductBarcode?,
    val note: String?,
    val image: BlobDigest?,
    val nutritionFacts: NutritionFacts,
    val servingQuantity: AbsoluteQuantity?,
    val packageQuantity: AbsoluteQuantity?,
    val isLiquid: Boolean,
) {
    init {
        require(brand == null || brand.isNotBlank()) { "Brand name cannot be blank" }
        require(note == null || note.isNotBlank()) { "Note cannot be blank" }
    }

    val headline: String = buildString {
        append(name)
        if (brand != null) {
            append(brand.let { " ($it)" })
        }
    }

    companion object {
        fun create(product: UserProduct, clock: Clock = Clock.System): List<UserProductEvent> =
            listOf(UserProductCreatedEvent(product = product, timestamp = clock.now()))
    }
}

@Serializable
@JvmInline
value class UserProductBarcode(val value: String) {
    init {
        require(value.isNotBlank()) { "Barcode cannot be blank" }
        require(value.all { it.isDigit() }) { "Barcode must contain only digits" }
    }
}

inline fun UserProduct.update(
    clock: Clock = Clock.System,
    transform: (UserProduct) -> UserProduct,
): List<UserProductEvent> = buildList {
    val updated = transform(this@update)
    if (updated != this@update)
        add(UserProductUpdatedEvent(product = updated, timestamp = clock.now()))
}

fun UserProduct.remove(
    strategy: DeleteStrategy,
    clock: Clock = Clock.System,
): List<UserProductEvent> =
    listOf(
        UserProductDeletedEvent(userProductId = id, strategy = strategy, timestamp = clock.now())
    )

fun UserProduct?.apply(event: UserProductEvent): UserProduct? =
    when (event) {
        is UserProductCreatedEvent -> event.product
        is UserProductUpdatedEvent -> event.product
        is UserProductDeletedEvent -> null
    }

fun Iterable<UserProductEvent>.toUserProduct(): UserProduct? =
    fold(null) { state, event -> state.apply(event) }

fun UserProduct.toSnapshot() =
    LeafFoodSnapshot(
        id = FoodSnapshotId.UserProduct(id.value),
        name = name,
        brand = brand,
        image = image?.let(FoodSnapshotImage::Blob),
        nutritionFacts = nutritionFacts,
    )

fun FoodSnapshotId.UserProduct.toUserProductId() = UserProductId(id)
