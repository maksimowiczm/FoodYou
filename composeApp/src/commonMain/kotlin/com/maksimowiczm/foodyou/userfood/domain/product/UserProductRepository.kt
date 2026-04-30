package com.maksimowiczm.foodyou.userfood.domain.product

import kotlinx.coroutines.flow.Flow

interface UserProductRepository {
    suspend fun save(product: UserProduct)

    fun observe(identity: UserProductIdentity): Flow<UserProduct?>

    suspend fun delete(identity: UserProductIdentity)
}
