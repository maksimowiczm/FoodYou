package com.maksimowiczm.foodyou.userproduct.application

import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.load
import com.maksimowiczm.foodyou.common.domain.observe
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userproduct.domain.remove
import com.maksimowiczm.foodyou.userproduct.domain.toUserProduct
import com.maksimowiczm.foodyou.userproduct.domain.update
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProductService(
    private val eventStore: EventStore,
    private val blobStorage: BlobStorage,
    private val eventBus: EventBus,
) {
    private fun streamId(identity: UserProductIdentity) = "UserProduct-${identity.id}"

    private suspend inline fun transact(
        identity: UserProductIdentity,
        block: (UserProduct?) -> List<UserProductEvent>,
    ) {
        val product = eventStore.load<UserProductEvent>(streamId(identity)).toUserProduct()
        val newEvents = block(product)
        if (newEvents.isNotEmpty()) {
            eventStore.append(streamId(identity), newEvents)
            eventBus.publish(newEvents)
        }
    }

    fun observe(identity: UserProductIdentity): Flow<UserProduct?> =
        eventStore.observe<UserProductEvent>(streamId(identity)).map { it.toUserProduct() }

    suspend fun create(
        name: FoodName,
        brand: String?,
        barcode: UserProductBarcode?,
        note: String?,
        imageBytes: ByteArray?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        isLiquid: Boolean,
    ): UserProductIdentity {
        val identity = UserProductIdentity(Uuid.random())
        transact(identity) {
            UserProduct.create(
                UserProduct(
                    identity = identity,
                    name = name,
                    brand = brand,
                    barcode = barcode,
                    note = note,
                    image = imageBytes?.let { blobStorage.store(it) },
                    nutritionFacts = nutritionFacts,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                    isLiquid = isLiquid,
                )
            )
        }
        return identity
    }

    suspend fun edit(
        identity: UserProductIdentity,
        name: FoodName,
        brand: String?,
        barcode: UserProductBarcode?,
        note: String?,
        imageBytes: ByteArray?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        isLiquid: Boolean,
    ) {
        transact(identity) { product ->
            checkNotNull(product) { "Product with ID $identity not found" }
            product.update {
                it.copy(
                    name = name,
                    brand = brand,
                    barcode = barcode,
                    note = note,
                    image = imageBytes?.let { bytes -> blobStorage.store(bytes) },
                    nutritionFacts = nutritionFacts,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                    isLiquid = isLiquid,
                )
            }
        }
    }

    suspend fun delete(identity: UserProductIdentity, strategy: DeleteStrategy) {
        transact(identity) { product ->
            checkNotNull(product) { "Product with ID $identity not found" }
            product.remove(strategy)
        }
    }
}
