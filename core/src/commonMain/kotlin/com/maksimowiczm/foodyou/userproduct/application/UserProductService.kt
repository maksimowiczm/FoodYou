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
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userproduct.domain.remove
import com.maksimowiczm.foodyou.userproduct.domain.toUserProduct
import com.maksimowiczm.foodyou.userproduct.domain.update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProductService(
    private val eventStore: EventStore,
    private val blobStorage: BlobStorage,
    private val eventBus: EventBus,
) {
    private fun streamId(id: UserProductId) = "UserProduct-${id.value}"

    private suspend inline fun transact(
        id: UserProductId,
        block: (UserProduct?) -> List<UserProductEvent>,
    ) {
        val product = eventStore.load<UserProductEvent>(streamId(id)).toUserProduct()
        val newEvents = block(product)
        if (newEvents.isNotEmpty()) {
            eventStore.append(streamId(id), newEvents)
            eventBus.publish(newEvents)
        }
    }

    fun observe(id: UserProductId): Flow<UserProduct?> =
        eventStore.observe<UserProductEvent>(streamId(id)).map { it.toUserProduct() }

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
    ): UserProductId {
        val id = UserProductId()
        transact(id) {
            UserProduct.create(
                UserProduct(
                    id = id,
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
        return id
    }

    suspend fun edit(
        id: UserProductId,
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
        transact(id) { product ->
            checkNotNull(product) { "Product with ID $id not found" }
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

    suspend fun delete(id: UserProductId, strategy: DeleteStrategy) {
        transact(id) { product ->
            checkNotNull(product) { "Product with ID $id not found" }
            product.remove(strategy)
        }
    }
}
