package com.maksimowiczm.foodyou.feature.food.recipe.presentation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import com.maksimowiczm.foodyou.feature.shared.ui.Saver
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.entity.RecipeIngredient
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement

/** Represents a minimal data structure for an ingredient. Easy to save and restore in the state. */
@Immutable
internal data class MinimalIngredient(val foodId: FoodId, val measurement: Measurement) {

    fun intoPair(): Pair<FoodId, Measurement> = foodId to measurement

    companion object {
        val ListSaver
            get() =
                Saver<List<MinimalIngredient>, List<Map<String, ArrayList<Any>?>>>(
                    save = {
                        it.map { ingredient ->
                            mapOf(
                                "foodId" to with(foodIdSaver) { save(ingredient.foodId) },
                                "measurement" to
                                    with(Measurement.Saver) { save(ingredient.measurement) },
                            )
                        }
                    },
                    restore =
                        @Suppress("UNCHECKED_CAST") {
                            it as List<Map<String, Any>>

                            it.map { map ->
                                val foodId =
                                    with(foodIdSaver) { restore(map["foodId"] as ArrayList<Any>) }
                                        ?: error("Failed to restore foodId")

                                val measurement =
                                    with(Measurement.Saver) {
                                        restore(map["measurement"] as ArrayList<Any>)
                                    } ?: error("Failed to restore measurement")

                                MinimalIngredient(foodId = foodId, measurement = measurement)
                            }
                        },
                )

        private val foodIdSaver: Saver<FoodId, ArrayList<Any>> =
            Saver(
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
                },
            )
    }
}

internal fun RecipeIngredient.toMinimalIngredient(): MinimalIngredient =
    MinimalIngredient(foodId = food.id, measurement = measurement)
