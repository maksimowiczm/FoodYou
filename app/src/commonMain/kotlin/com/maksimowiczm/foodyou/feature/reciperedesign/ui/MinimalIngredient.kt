package com.maksimowiczm.foodyou.feature.reciperedesign.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.Saver
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.res.Saver

/**
 * Represents a minimal data structure for an ingredient. Easy to save and restore in the state.
 */
@Immutable
internal data class MinimalIngredient(val foodId: FoodId, val measurement: Measurement) {
    companion object {
        val ListSaver
            get() = Saver<List<MinimalIngredient>, List<Map<String, ArrayList<Any>?>>>(
                save = {
                    it.map { ingredient ->
                        mapOf(
                            "foodId" to with(foodIdSaver) { save(ingredient.foodId) },
                            "measurement" to
                                with(Measurement.Saver) { save(ingredient.measurement) }
                        )
                    }
                },
                restore = @Suppress("UNCHECKED_CAST") {
                    it as List<Map<String, Any>>

                    it.map { map ->
                        val foodId = with(foodIdSaver) {
                            restore(map["foodId"] as ArrayList<Any>)
                        } ?: error("Failed to restore foodId")

                        val measurement = with(Measurement.Saver) {
                            restore(map["measurement"] as ArrayList<Any>)
                        } ?: error("Failed to restore measurement")

                        MinimalIngredient(
                            foodId = foodId,
                            measurement = measurement
                        )
                    }
                }
            )
    }
}

private val foodIdSaver: Saver<FoodId, ArrayList<Any>> = Saver(
    save = {
        when (it) {
            is FoodId.Product -> arrayListOf(0, it.id)
            is FoodId.Recipe -> arrayListOf(1, it.id)
        }
    },
    restore = {
        when (it[0] as Int) {
            0 -> FoodId.Product(it[1] as Long)
            1 -> FoodId.Recipe(it[1] as Long)
            else -> error("Invalid foodId type")
        }
    }
)
