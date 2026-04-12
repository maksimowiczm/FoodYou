package com.maksimowiczm.foodyou.userfood.infrastructure.product

import com.maksimowiczm.foodyou.common.domain.ImageUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.infrastructure.filekit.FileKitBlobStorage
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBarcode
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBrand
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductRepository
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.ProductDao
import io.github.vinceglb.filekit.PlatformFile
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class UserProductRepositoryImpl(
    private val dao: ProductDao,
    private val integrationEventBus: EventBus<IntegrationEvent>,
    private val blobStorage: FileKitBlobStorage,
) : UserProductRepository {
    private val mapper = ProductMapper()

    override suspend fun create(
        name: FoodName,
        brand: UserProductBrand?,
        barcode: UserProductBarcode?,
        note: UserFoodNote?,
        image: ImageUri?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        isLiquid: Boolean,
    ): UserProductIdentity {
        val uuid = Uuid.random()

        val imageBlobPath =
            if (image != null) {
                val digest = blobStorage.store(PlatformFile(image.value))
                blobStorage.uri(digest).value
            } else {
                null
            }

        val entity =
            mapper.toEntity(
                uuid = uuid,
                name = name,
                brand = brand,
                barcode = barcode,
                note = note,
                imagePath = imageBlobPath,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )

        dao.insert(entity)

        return UserProductIdentity(uuid)
    }

    override suspend fun edit(
        identity: UserProductIdentity,
        name: FoodName,
        brand: UserProductBrand?,
        barcode: UserProductBarcode?,
        note: UserFoodNote?,
        image: ImageUri?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        isLiquid: Boolean,
    ) {
        val existingEntity = dao.observe(identity.id).first()

        requireNotNull(existingEntity) {
            "Cannot edit non-existing food product with id: ${identity.id}"
        }

        val uuid = identity.id

        val imagePath: String? =
            if (existingEntity.photoPath != image?.value) {
                if (image != null) {
                    val digest = blobStorage.store(PlatformFile(image.value))
                    blobStorage.uri(digest).value
                } else {
                    null
                }
            } else {
                existingEntity.photoPath
            }

        val updatedEntity =
            mapper.toEntity(
                id = existingEntity.sqliteId,
                uuid = uuid,
                name = name,
                brand = brand,
                barcode = barcode,
                note = note,
                imagePath = imagePath,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                isLiquid = isLiquid,
            )

        dao.update(updatedEntity)
    }

    override fun observe(identity: UserProductIdentity): Flow<UserProduct?> =
        dao.observe(identity.id).map { entity -> entity?.let(mapper::userProduct) }

    override suspend fun delete(identity: UserProductIdentity) {
        val existingEntity = dao.observe(identity.id).first()

        requireNotNull(existingEntity) {
            "Cannot delete non-existing food product with id: ${identity.id}"
        }

        dao.delete(existingEntity)

        integrationEventBus.publish(UserProductDeletedEvent(identity))
    }
}
