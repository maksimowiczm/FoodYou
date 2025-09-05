package com.maksimowiczm.foodyou.navigation.domain

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.domain.measurement.MeasurementType
import com.maksimowiczm.foodyou.business.shared.domain.measurement.from
import com.maksimowiczm.foodyou.business.shared.domain.measurement.rawValue
import com.maksimowiczm.foodyou.business.shared.domain.measurement.type
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
internal data class FoodDiarySearchDestination(val epochDay: Long, val mealId: Long) {
    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)
}

@Serializable
internal data class FoodDiaryAddEntryDestination(
    val productId: Long?,
    val recipeId: Long?,
    val mealId: Long,
    val epochDay: Long,
    val measurementType: MeasurementType?,
    val quantity: Double?,
) {
    init {
        if (productId == null && recipeId == null) {
            error("Either productId or recipeId must be provided")
        }

        if (
            (measurementType != null && quantity == null) ||
                (measurementType == null && quantity != null)
        ) {
            error("If measurementType is provided, quantity must also be provided")
        }
    }

    constructor(
        foodId: FoodId,
        mealId: Long,
        date: LocalDate,
        measurement: Measurement?,
    ) : this(
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
        mealId = mealId,
        epochDay = date.toEpochDays(),
        measurementType = measurement?.type,
        quantity = measurement?.rawValue,
    )

    val foodId: FoodId
        get() =
            when {
                productId != null -> FoodId.Product(productId)
                recipeId != null -> FoodId.Recipe(recipeId)
                else -> error("Either productId or recipeId must be provided")
            }

    val measurement: Measurement?
        get() =
            if (measurementType != null && quantity != null) {
                Measurement.from(measurementType, quantity)
            } else {
                null
            }

    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)
}

@Serializable internal data class UpdateFoodDiaryEntryDestination(val entryId: Long)

@Serializable
internal data class FoodDiaryCreateProductDestination(val mealId: Long, val epochDay: Long) {
    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)

    constructor(mealId: Long, date: LocalDate) : this(mealId, date.toEpochDays())
}

@Serializable
internal data class FoodDiaryCreateRecipeDestination(val mealId: Long, val epochDay: Long) {
    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)

    constructor(mealId: Long, date: LocalDate) : this(mealId, date.toEpochDays())
}

@Serializable
internal data class FoodDiaryCreateQuickAdd(val mealId: Long, val epochDay: Long) {
    val date: LocalDate
        get() = LocalDate.fromEpochDays(epochDay)
}

@Serializable internal data class FoodDiaryUpdateQuickAdd(val entryId: Long)
