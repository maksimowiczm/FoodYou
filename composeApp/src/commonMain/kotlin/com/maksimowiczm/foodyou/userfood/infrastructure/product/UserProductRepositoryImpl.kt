package com.maksimowiczm.foodyou.userfood.infrastructure.product

import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class UserProductRepositoryImpl(private val dao: ProductDao) : UserProductRepository {
    private val mapper = ProductMapper()

    override suspend fun save(product: UserProduct) {
        val existingEntity = dao.observe(product.identity.id).first()

        val entity =
            mapper.toEntity(
                id = existingEntity?.sqliteId ?: 0,
                uuid = product.identity.id,
                name = product.name,
                brand = product.brand,
                barcode = product.barcode,
                note = product.note,
                imageDigest = product.image,
                nutritionFacts = product.nutritionFacts,
                servingQuantity = product.servingQuantity,
                packageQuantity = product.packageQuantity,
                isLiquid = product.isLiquid,
            )

        if (existingEntity == null) {
            dao.insert(entity)
        } else {
            dao.update(entity)
        }
    }

    override fun observe(identity: UserProductIdentity): Flow<UserProduct?> =
        dao.observe(identity.id).map { entity -> entity?.let(mapper::userProduct) }

    override suspend fun delete(identity: UserProductIdentity) {
        val existingEntity = dao.observe(identity.id).first()

        requireNotNull(existingEntity) {
            "Cannot delete non-existing food product with id: ${identity.id}"
        }

        dao.delete(existingEntity)
    }
}
