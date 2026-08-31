package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import kotlin.uuid.Uuid

class FoodSnapshotIdConverter {
    @ColumnTypeConverter
    fun fromIdentity(identity: FoodSnapshotId.Tracked): String =
        when (identity) {
            is FoodSnapshotId.UserProduct -> "$USER_PRODUCT$SEPARATOR${identity.id}"
            is FoodSnapshotId.OpenFoodFacts -> "$OPEN_FOOD_FACTS$SEPARATOR${identity.barcode}"
            is FoodSnapshotId.FoodDataCentral -> "$FOOD_DATA_CENTRAL$SEPARATOR${identity.fdcId}"
            is FoodSnapshotId.UserRecipe -> "$RECIPE$SEPARATOR${identity.id}"
        }

    @ColumnTypeConverter
    fun toIdentity(value: String): FoodSnapshotId.Tracked {
        val (type, idValue) = value.split(SEPARATOR, limit = 2)
        return when (type) {
            USER_PRODUCT -> FoodSnapshotId.UserProduct(Uuid.parse(idValue))
            OPEN_FOOD_FACTS -> FoodSnapshotId.OpenFoodFacts(idValue)
            FOOD_DATA_CENTRAL -> FoodSnapshotId.FoodDataCentral(idValue.toInt())
            RECIPE -> FoodSnapshotId.UserRecipe(Uuid.parse(idValue))
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
