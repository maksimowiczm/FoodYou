package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room3.*
import kotlin.uuid.Uuid

class UuidConverter {
    @ColumnTypeConverter fun fromUuid(uuid: Uuid): String = uuid.toString()

    @ColumnTypeConverter fun toUuid(uuidString: String): Uuid = Uuid.parse(uuidString)
}
