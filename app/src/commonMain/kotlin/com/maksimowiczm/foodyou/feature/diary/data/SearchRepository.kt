package com.maksimowiczm.foodyou.feature.diary.data

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.database.entity.DiarySearchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface SearchRepository {
    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>

    fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<PagingData<DiarySearchModel>>
}

data class DiarySearchModel(
    val uniqueId: String,
    val foodId: FoodId,
    val measurementId: MeasurementId?,
    val name: String,
    val brand: String?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val weightMeasurement: WeightMeasurement
)

fun DiarySearchEntity.toSearchModel(): DiarySearchModel {
    val weightMeasurement = when {
        measurement == null || quantity == null -> {
            when {
                servingWeight != null -> WeightMeasurement.Serving(1f)
                packageWeight != null -> WeightMeasurement.Package(1f)
                else -> WeightMeasurement.WeightUnit(100f)
            }
        }

        else -> when (measurement) {
            WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(quantity)
            WeightMeasurementEnum.Package -> {
                assert(packageWeight != null) {
                    "Package weight should not be null for package measurement"
                }
                WeightMeasurement.Package(quantity)
            }

            WeightMeasurementEnum.Serving -> WeightMeasurement.Serving(quantity)
        }
    }

    return if (productId != null) {
        if (weightMeasurement is WeightMeasurement.Serving) {
            assert(servingWeight != null) {
                "Serving weight should not be null for serving measurement"
            }
        }

        DiarySearchModel(
            uniqueId = "p_${productId}_$measurementId",
            foodId = FoodId.Product(productId),
            measurementId = measurementId?.let { MeasurementId.Product(measurementId) },
            name = name,
            brand = brand,
            calories = calories,
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            weightMeasurement = weightMeasurement
        )
    } else if (recipeId != null) {
        if (packageWeight == null) {
            error("Package weight should not be null for recipe measurement")
        }
        if (servings == null) {
            error("Servings should not be null for recipe measurement")
        }

        val servingWeight = packageWeight / servings

        DiarySearchModel(
            uniqueId = "r_${recipeId}_$measurementId",
            foodId = FoodId.Recipe(recipeId),
            measurementId = measurementId?.let { MeasurementId.Recipe(measurementId) },
            name = name,
            brand = brand,
            calories = calories,
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            weightMeasurement = weightMeasurement
        )
    } else {
        error("Database corruption for search entity: $this")
    }
}
