package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

class FoodSnapshotConverter {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(FoodSnapshot::class) {
                subclassesOfSealed<FoodSnapshot>()
            }
        }
    }

    @ColumnTypeConverter
    fun fromSnapshot(value: MeasuredFoodSnapshot): String = json.encodeToString(value)

    @ColumnTypeConverter
    fun toSnapshot(value: String): MeasuredFoodSnapshot = json.decodeFromString(value)
}
