package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.uuid.Uuid

fun testProfileId(id: Uuid = Uuid.random()): ProfileId = ProfileId(id)
