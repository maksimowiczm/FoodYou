package com.maksimowiczm.foodyou.userfood.application

import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBarcode
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBrand
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductRepository
import kotlin.time.Clock
import kotlin.uuid.Uuid

class UserProductService(
    private val repository: UserProductRepository,
    private val blobStorage: BlobStorage,
    private val eventBus: EventBus,
) {
    suspend fun create(
        name: FoodName,
        brand: UserProductBrand?,
        barcode: UserProductBarcode?,
        note: UserFoodNote?,
        imageBytes: ByteArray?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        isLiquid: Boolean,
    ): UserProductIdentity {
        val identity = UserProductIdentity(Uuid.random())
        val imageDigest = imageBytes?.let { blobStorage.store(it) }

        val product =
            UserProduct(
                identity = identity,
                name = name,
                brand = brand,
                barcode = barcode,
                note = note,
                image = imageDigest,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )

        repository.save(product)

        return identity
    }

    suspend fun edit(
        identity: UserProductIdentity,
        name: FoodName,
        brand: UserProductBrand?,
        barcode: UserProductBarcode?,
        note: UserFoodNote?,
        imageBytes: ByteArray?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        isLiquid: Boolean,
    ) {
        val imageDigest = imageBytes?.let { blobStorage.store(it) }

        val product =
            UserProduct(
                identity = identity,
                name = name,
                brand = brand,
                barcode = barcode,
                note = note,
                image = imageDigest,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )

        repository.save(product)
    }

    suspend fun delete(identity: UserProductIdentity) {
        repository.delete(identity)
        eventBus.publish(
            UserProductDeletedEvent(identity = identity, timestamp = Clock.System.now())
        )
    }
}
