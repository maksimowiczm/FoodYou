package com.maksimowiczm.foodyou.common.domain

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable @JvmInline value class ProfileId(val value: Uuid)
