package com.maksimowiczm.foodyou.common.domain

import kotlinx.serialization.Serializable

@Serializable
enum class DeleteStrategy {
    Delete,
    Unlink,
}
