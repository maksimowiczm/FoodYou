package com.maksimowiczm.foodyou.userfood.domain.product

import kotlin.uuid.Uuid

/**
 * Local database identifier for user-created or imported food products.
 *
 * @property id The unique identifier within the local database
 */
data class UserProductIdentity(val id: Uuid)
