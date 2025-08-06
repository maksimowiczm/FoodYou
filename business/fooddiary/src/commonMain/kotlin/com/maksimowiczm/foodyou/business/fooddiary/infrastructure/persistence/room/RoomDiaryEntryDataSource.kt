package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodProduct
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeIngredientEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntityNutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntityType
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntityValue
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

internal class RoomDiaryEntryDataSource(private val measurementDao: MeasurementDao) :
    LocalDiaryEntryDataSource {

    @OptIn(ExperimentalTime::class)
    override suspend fun insert(diaryEntry: DiaryEntry): Long {

        // The issue here is that it doesn't run in a transaction, so if one of the inserts fails,
        // the other one will still be committed. It's not a big issue because the measurement is
        // inserted only after food is inserted, so if the food insertion fails, it will just dangle
        // in the database without a measurement.

        val recipeId = run {
            if (diaryEntry.food is DiaryFoodRecipe) {
                insertRecipe(diaryEntry.food)
            } else {
                null
            }
        }

        val productId = run {
            if (diaryEntry.food is DiaryFoodProduct) {
                insertProduct(diaryEntry.food)
            } else {
                null
            }
        }

        if (recipeId == null && productId == null) {
            error("Diary entry must have either a product or a recipe")
        }

        val entity =
            MeasurementEntity(
                mealId = diaryEntry.mealId,
                epochDay = diaryEntry.date.toEpochDays(),
                productId = productId,
                recipeId = recipeId,
                measurement = diaryEntry.measurement.toEntityType(),
                quantity = diaryEntry.measurement.toEntityValue(),
                createdAt =
                    diaryEntry.createdAt.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
            )

        return measurementDao.insertMeasurement(entity)
    }

    override suspend fun update(diaryEntry: DiaryEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(diaryEntry: DiaryEntry) {
        val measurement = measurementDao.observeMeasurementById(diaryEntry.id).firstOrNull()

        if (measurement == null) {
            error("Measurement with id ${diaryEntry.id} not found")
        }

        measurementDao.deleteMeasurement(measurement)
    }

    private suspend fun insertProduct(diaryFoodProduct: DiaryFoodProduct): Long {
        val entity = diaryFoodProduct.toEntity()
        return measurementDao.insertDiaryProduct(entity)
    }

    private suspend fun insertRecipe(diaryRecipe: DiaryFoodRecipe): Long {

        val recipe = DiaryRecipeEntity(name = diaryRecipe.name, servings = diaryRecipe.servings)

        val recipeId = measurementDao.insertDiaryRecipe(recipe)

        diaryRecipe.ingredients.forEach { ingredient ->
            val ingredientRecipeId = run {
                if (ingredient.food is DiaryFoodRecipe) {
                    insertRecipe(ingredient.food)
                } else {
                    null
                }
            }

            val ingredientProductId = run {
                if (ingredient.food is DiaryFoodProduct) {
                    insertProduct(ingredient.food)
                } else {
                    null
                }
            }

            val ingredientEntity =
                DiaryRecipeIngredientEntity(
                    recipeId = recipeId,
                    ingredientProductId = ingredientProductId,
                    ingredientRecipeId = ingredientRecipeId,
                    measurement = ingredient.measurement.toEntityType(),
                    quantity = ingredient.measurement.toEntityValue(),
                )

            measurementDao.insertDiaryRecipeIngredient(ingredientEntity)
        }

        return recipeId
    }
}

private fun DiaryFoodProduct.toEntity(): DiaryProductEntity {
    val (nutrients, vitamins, minerals) = toEntityNutrients(nutritionFacts)

    return DiaryProductEntity(
        name = name,
        nutrients = nutrients,
        vitamins = vitamins,
        minerals = minerals,
        servingWeight = servingWeight,
        packageWeight = totalWeight,
    )
}
