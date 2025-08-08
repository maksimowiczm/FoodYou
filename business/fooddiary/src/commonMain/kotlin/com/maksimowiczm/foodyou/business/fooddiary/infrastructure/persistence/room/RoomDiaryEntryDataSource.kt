package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodProduct
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipeIngredient
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeIngredientEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.measurementFrom
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntityNutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntityType
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntityValue
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toNutritionFacts
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal class RoomDiaryEntryDataSource(private val measurementDao: MeasurementDao) :
    LocalDiaryEntryDataSource {

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
    override fun observeEntries(mealId: Long, date: LocalDate): Flow<List<DiaryEntry>> =
        measurementDao
            .observeMeasurements(mealId = mealId, epochDay = date.toEpochDays())
            .flatMapLatest { entities ->
                if (entities.isEmpty()) {
                    return@flatMapLatest flowOf(emptyList())
                }

                entities
                    .map { entity ->
                        val foodFlow = observeFood(entity)
                        val createdAt =
                            Instant.fromEpochSeconds(entity.createdAt)
                                .toLocalDateTime(TimeZone.currentSystemDefault())

                        foodFlow.map {
                            DiaryEntry(
                                id = entity.id,
                                mealId = entity.mealId,
                                date = LocalDate.fromEpochDays(entity.epochDay),
                                measurement = measurementFrom(entity.measurement, entity.quantity),
                                food = it,
                                createdAt = createdAt,
                            )
                        }
                    }
                    .combine()
            }

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

    private fun observeFood(measurementEntity: MeasurementEntity): Flow<DiaryFood> =
        measurementEntity.productId?.let { productId -> observeProduct(productId) }
            ?: measurementEntity.recipeId?.let { recipeId -> observeRecipe(recipeId) }
            ?: error("Measurement entity must have either a product or a recipe")

    private fun observeProduct(productId: Long): Flow<DiaryFoodProduct> =
        measurementDao.observeDiaryProduct(productId).filterNotNull().map { entity ->
            entity.toModel()
        }

    private suspend fun insertRecipe(diaryRecipe: DiaryFoodRecipe): Long {

        val recipe =
            DiaryRecipeEntity(
                name = diaryRecipe.name,
                servings = diaryRecipe.servings,
                isLiquid = diaryRecipe.isLiquid,
            )

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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeRecipe(recipeId: Long): Flow<DiaryFoodRecipe> =
        measurementDao
            .observeDiaryRecipe(recipeId)
            .filterNotNull()
            .map { entity ->
                val ingredients =
                    measurementDao
                        .observeDiaryRecipeIngredients(entity.id)
                        .map { ingredients ->
                            ingredients
                                .map { ingredient ->
                                    ingredient.ingredientProductId?.let {
                                        observeProduct(it).filterNotNull().map {
                                            measurementFrom(
                                                ingredient.measurement,
                                                ingredient.quantity,
                                            ) to it
                                        }
                                    }
                                        ?: ingredient.ingredientRecipeId?.let { recipeId ->
                                            observeRecipe(recipeId).filterNotNull().map {
                                                measurementFrom(
                                                    ingredient.measurement,
                                                    ingredient.quantity,
                                                ) to it
                                            }
                                        }
                                        ?: error(
                                            "Recipe ingredient must have either a product or a recipe"
                                        )
                                }
                                .combine()
                        }
                        .flatMapLatest { it }
                        .map { list ->
                            list.map { (measurement, food) ->
                                DiaryFoodRecipeIngredient(food = food, measurement = measurement)
                            }
                        }

                ingredients.map { ingredients ->
                    DiaryFoodRecipe(
                        name = entity.name,
                        servings = entity.servings,
                        ingredients = ingredients,
                        isLiquid = entity.isLiquid,
                    )
                }
            }
            .flatMapLatest { it }
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
        isLiquid = isLiquid,
    )
}

private fun DiaryProductEntity.toModel(): DiaryFoodProduct =
    DiaryFoodProduct(
        name = name,
        nutritionFacts = toNutritionFacts(nutrients, vitamins, minerals),
        servingWeight = servingWeight,
        totalWeight = packageWeight,
        isLiquid = isLiquid,
    )
