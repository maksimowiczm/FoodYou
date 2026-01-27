package com.maksimowiczm.foodyou.userfood.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId

/**
 * Local database identifier for user-created or imported food products.
 *
 * @property id The unique identifier within the local database
 * @property accountId The account that owns this food product
 */
data class UserFoodProductIdentity(val id: String, val accountId: LocalAccountId)
