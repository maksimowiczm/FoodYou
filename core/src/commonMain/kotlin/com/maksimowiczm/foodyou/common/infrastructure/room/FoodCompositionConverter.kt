package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

class FoodCompositionConverter {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(FoodCompositionComponent::class) {
                subclassesOfSealed<FoodCompositionComponent>()
            }
        }
    }

    @ColumnTypeConverter
    fun fromComposition(value: FoodCompositionComponent): String = json.encodeToString(value)

    @ColumnTypeConverter
    fun toComposition(value: String): FoodCompositionComponent = json.decodeFromString(value)
}
