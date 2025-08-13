package com.maksimowiczm.foodyou.feature.food.diary.update.presentation

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.MeasurementType

val DiaryFood.possibleMeasurementTypes: List<MeasurementType>
    get() {
        val types = mutableListOf<MeasurementType>()

        if (totalWeight != null) {
            types.add(MeasurementType.Package)
        }

        if (servingWeight != null) {
            types.add(MeasurementType.Serving)
        }

        if (isLiquid) {
            types.add(MeasurementType.Milliliter)
        } else {
            types.add(MeasurementType.Gram)
        }

        return types
    }

val DiaryFood.suggestions: List<Measurement>
    get() =
        possibleMeasurementTypes.map {
            when (it) {
                MeasurementType.Gram -> Measurement.Gram(100.0)
                MeasurementType.Package -> Measurement.Package(1.0)
                MeasurementType.Serving -> Measurement.Serving(1.0)
                MeasurementType.Milliliter -> Measurement.Milliliter(100.0)
            }
        }

val DiaryFood.canUnpack: Boolean
    get() = this is DiaryFoodRecipe

fun DiaryFoodRecipe.allIngredients(): List<DiaryFood> =
    ingredients.flatMap {
        if (it.food is DiaryFoodRecipe) {
            (it.food as DiaryFoodRecipe).allIngredients()
        } else {
            listOf(it.food)
        }
    }
