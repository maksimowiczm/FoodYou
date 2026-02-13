package com.maksimowiczm.foodyou.userfood.infrastructure.product

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Barcode
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.infrastructure.filekit.directory
import com.maksimowiczm.foodyou.userfood.domain.UserFoodNote
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductBrand
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductRepository
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductSource
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.ProductDao
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class UserProductRepositoryImpl(
    private val dao: ProductDao,
    private val integrationEventBus: EventBus<IntegrationEvent>,
) : UserProductRepository {
    private val mapper = ProductMapper()

    override suspend fun create(
        name: FoodName,
        brand: UserProductBrand?,
        barcode: Barcode?,
        note: UserFoodNote?,
        imageUri: String?,
        source: UserProductSource?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    ): UserProductIdentity {
        val foodDirectory = accountId.directory() / "food"
        foodDirectory.createDirectories()

        val uuid = Uuid.random().toString()

        val photoPath =
            if (imageUri != null) {
                val sourceFile = PlatformFile(imageUri)
                require(sourceFile.exists()) { "Image file does not exist at path: $imageUri" }
                val bytes = sourceFile.readBytes()

                val compressed =
                    FileKit.compressImage(
                        bytes = bytes,
                        quality = 85,
                        imageFormat = ImageFormat.JPEG,
                    )

                val dest = (foodDirectory / "$uuid.jpg").apply { write(compressed) }
                dest.path
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
                imagePath = photoPath,
                source = source,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                accountId = accountId,
                isLiquid = isLiquid,
            )

        dao.insert(entity)

        return UserProductIdentity(uuid, LocalAccountId(entity.accountId))
    }

    override suspend fun edit(
        identity: UserProductIdentity,
        name: FoodName,
        brand: UserProductBrand?,
        barcode: Barcode?,
        note: UserFoodNote?,
        imageUri: String?,
        source: UserProductSource?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    ) {
        val existingEntity = dao.observe(identity.id, accountId.value).first()

        requireNotNull(existingEntity) {
            "Cannot edit non-existing food product with id: ${identity.id}"
        }

        val uuid = identity.id
        val foodDirectory = accountId.directory() / "food"
        foodDirectory.createDirectories()

        val imagePath: String? =
            if (existingEntity.photoPath != imageUri) {
                if (imageUri != null) {
                    val sourceFile = PlatformFile(imageUri)
                    require(sourceFile.exists()) { "Image file does not exist at path: $imageUri" }
                    val bytes = sourceFile.readBytes()

                    val compressed =
                        FileKit.compressImage(
                            bytes = bytes,
                            quality = 85,
                            imageFormat = ImageFormat.JPEG,
                        )

                    val dest = (foodDirectory / "$uuid.jpg").apply { write(compressed) }

                    dest.path
                } else {
                    existingEntity.photoPath?.let { existingPath ->
                        val existingFile = PlatformFile(existingPath)
                        if (existingFile.exists()) {
                            existingFile.delete()
                        }
                    }
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
                source = source,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                accountId = accountId,
                isLiquid = isLiquid,
            )

        dao.update(updatedEntity)
    }

    override fun observe(identity: UserProductIdentity): Flow<UserProduct?> =
        dao.observe(identity.id, identity.accountId.value).map { entity ->
            entity?.let(mapper::userProduct)
        }

    override suspend fun delete(identity: UserProductIdentity) {
        val existingEntity = dao.observe(identity.id, identity.accountId.value).first()

        requireNotNull(existingEntity) {
            "Cannot delete non-existing food product with id: ${identity.id}"
        }

        dao.delete(existingEntity)
        PlatformFile("${existingEntity.uuid}.jpg").delete(mustExist = false)

        integrationEventBus.publish(UserProductDeletedEvent(identity))
    }
}
