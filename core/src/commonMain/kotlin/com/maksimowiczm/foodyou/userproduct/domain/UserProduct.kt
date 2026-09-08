@file:MustUseReturnValues

package com.maksimowiczm.foodyou.userproduct.domain

import com.maksimowiczm.foodyou.common.Decider
import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.TrackedLeafFoodSnapshot
import kotlin.jvm.JvmInline
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
}

@Serializable
@JvmInline
value class UserProductBarcode(val value: String) {
    init {
        require(value.isNotBlank()) { "Barcode cannot be blank" }
        require(value.all { it.isDigit() }) { "Barcode must contain only digits" }
    }
}

fun UserProduct?.decide(command: UserProductCommand): List<UserProductEvent> =
    when (command) {
        is UserProductCommand.Create ->
            if (this == null) {
                listOf(
                    UserProductCreatedEvent(
                        product = command.product,
                        timestamp = command.timestamp,
                    )
                )
            } else {
                emptyList()
            }

        is UserProductCommand.Update ->
            if (this != null) {
                val updated = command.transform(this)
                if (updated != this) {
                    listOf(
                        UserProductUpdatedEvent(product = updated, timestamp = command.timestamp)
                    )
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }

        is UserProductCommand.Remove ->
            if (this != null) {
                listOf(
                    UserProductDeletedEvent(
                        userProductId = id,
                        strategy = command.strategy,
                        timestamp = command.timestamp,
                    )
                )
            } else {
                emptyList()
            }
    }

fun UserProduct?.apply(event: UserProductEvent): UserProduct? =
    when (event) {
        is UserProductCreatedEvent -> event.product
        is UserProductUpdatedEvent -> event.product
        is UserProductDeletedEvent -> null
    }

fun Iterable<UserProductEvent>.toUserProduct(): UserProduct? =
    fold(null) { state, event -> state.apply(event) }

fun UserProduct.toSnapshot() =
    TrackedLeafFoodSnapshot(
        id = FoodSnapshotId.UserProduct(id.value),
        name = name,
        brand = brand,
        image = image?.let(FoodSnapshotImage::Blob),
        nutritionFacts = nutritionFacts,
    )

fun FoodSnapshotId.UserProduct.toUserProductId() = UserProductId(id)

val userProductDecider =
    Decider<UserProductCommand, UserProductEvent, UserProduct?>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = null,
    )
