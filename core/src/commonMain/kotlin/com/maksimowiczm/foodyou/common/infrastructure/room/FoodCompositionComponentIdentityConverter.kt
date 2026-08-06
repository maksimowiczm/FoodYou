package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import kotlin.uuid.Uuid

class FoodCompositionComponentIdentityConverter {
    @TypeConverter
    fun fromIdentity(identity: FoodCompositionComponentIdentity.Identified): String =
        when (identity) {
            is FoodCompositionComponentIdentity.UserProduct ->
                "$USER_PRODUCT$SEPARATOR${identity.id}"
            is FoodCompositionComponentIdentity.OpenFoodFacts ->
                "$OPEN_FOOD_FACTS$SEPARATOR${identity.barcode}"
            is FoodCompositionComponentIdentity.FoodDataCentral ->
                "$FOOD_DATA_CENTRAL$SEPARATOR${identity.fdcId}"
            is FoodCompositionComponentIdentity.Recipe -> "$RECIPE$SEPARATOR${identity.id}"
        }

    @TypeConverter
    fun toIdentity(value: String): FoodCompositionComponentIdentity.Identified {
        val (type, idValue) = value.split(SEPARATOR, limit = 2)
        return when (type) {
            USER_PRODUCT -> FoodCompositionComponentIdentity.UserProduct(Uuid.parse(idValue))
            OPEN_FOOD_FACTS -> FoodCompositionComponentIdentity.OpenFoodFacts(idValue)
            FOOD_DATA_CENTRAL -> FoodCompositionComponentIdentity.FoodDataCentral(idValue.toInt())
            RECIPE -> FoodCompositionComponentIdentity.Recipe(Uuid.parse(idValue))
            else -> error("Unknown FoodCompositionComponentIdentity type: $type")
        }
    }

    companion object {
        const val SEPARATOR = ":"
        const val USER_PRODUCT = "USER_PRODUCT"
        const val OPEN_FOOD_FACTS = "OPEN_FOOD_FACTS"
        const val FOOD_DATA_CENTRAL = "FOOD_DATA_CENTRAL"
        const val RECIPE = "RECIPE"
    }
}
