package com.maksimowiczm.foodyou.features.home.integration

import androidx.room3.*
import kotlinx.serialization.json.Json

class HomeEntryPayloadConverter {
    @ColumnTypeConverter
    fun fromPayload(value: HomeEntryPayload): String = Json.encodeToString(value)

    @ColumnTypeConverter
    fun toPayload(value: String): HomeEntryPayload = Json.decodeFromString(value)
}
