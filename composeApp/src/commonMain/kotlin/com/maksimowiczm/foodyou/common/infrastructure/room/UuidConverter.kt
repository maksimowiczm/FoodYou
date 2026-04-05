package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

class UuidConverter {
    @TypeConverter fun fromUuid(uuid: Uuid): String = uuid.toString()

    @TypeConverter fun toUuid(uuidString: String): Uuid = Uuid.parse(uuidString)
}
