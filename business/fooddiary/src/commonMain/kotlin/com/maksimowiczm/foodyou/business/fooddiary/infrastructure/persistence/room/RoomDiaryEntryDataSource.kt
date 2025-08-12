package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodProduct
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipeIngredient
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeIngredientEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toDomain
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toEntityNutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.toNutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.from
import com.maksimowiczm.foodyou.shared.common.domain.measurement.rawValue
import com.maksimowiczm.foodyou.shared.common.domain.measurement.type
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
internal class RoomDiaryEntryDataSource(private val measurementDao: MeasurementDao) :
    LocalDiaryEntryDataSource {

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
                        val updatedAt =
                            Instant.fromEpochSeconds(entity.updatedAt)
                                .toLocalDateTime(TimeZone.currentSystemDefault())

                        foodFlow.map {
                            DiaryEntry(
                                id = entity.id,
                                mealId = entity.mealId,
                                date = LocalDate.fromEpochDays(entity.epochDay),
                                measurement = Measurement.from(entity.measurement, entity.quantity),
                                food = it,
                                createdAt = createdAt,
                                updatedAt = updatedAt,
                            )
                        }
                    }
                    .combine()
            }

    override fun observeEntry(entryId: Long): Flow<DiaryEntry?> {
        return measurementDao.observeMeasurementById(entryId).filterNotNull().flatMapLatest { entity
            ->
            observeFood(entity).map { food ->
                val createdAt =
                    Instant.fromEpochSeconds(entity.createdAt)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                val updatedAt =
                    Instant.fromEpochSeconds(entity.updatedAt)
                        .toLocalDateTime(TimeZone.currentSystemDefault())

                DiaryEntry(
                    id = entity.id,
                    mealId = entity.mealId,
                    date = LocalDate.fromEpochDays(entity.epochDay),
                    measurement = Measurement.from(entity.measurement, entity.quantity),
                    food = food,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                )
            }
        }
    }

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
                measurement = diaryEntry.measurement.type,
                quantity = diaryEntry.measurement.rawValue,
                createdAt =
                    diaryEntry.createdAt.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
                updatedAt =
                    diaryEntry.updatedAt.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
            )

        return measurementDao.insertMeasurement(entity)
    }

    override suspend fun update(diaryEntry: DiaryEntry) {
        val entity = measurementDao.observeMeasurementById(diaryEntry.id).firstOrNull()

        if (entity == null) {
            error("Measurement with id ${diaryEntry.id} not found")
        }

        // Delete the existing product or recipe if it exists
        val productId = entity.productId
        val recipeId = entity.recipeId
        if (productId != null) {
            deleteProduct(productId)
        } else if (recipeId != null) {
            deleteRecipe(recipeId)
        }

        // Insert the new product or recipe
        val newProductId = run {
            if (diaryEntry.food is DiaryFoodProduct) {
                insertProduct(diaryEntry.food)
            } else {
                null
            }
        }
        val newRecipeId = run {
            if (diaryEntry.food is DiaryFoodRecipe) {
                insertRecipe(diaryEntry.food)
            } else {
                null
            }
        }

        if (newProductId == null && newRecipeId == null) {
            error("Diary entry must have either a product or a recipe")
        }

        val updatedEntity =
            entity.copy(
                mealId = diaryEntry.mealId,
                epochDay = diaryEntry.date.toEpochDays(),
                productId = newProductId,
                recipeId = newRecipeId,
                measurement = diaryEntry.measurement.type,
                quantity = diaryEntry.measurement.rawValue,
                updatedAt =
                    diaryEntry.updatedAt.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
            )

        measurementDao.updateMeasurement(updatedEntity)
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
                sourceType = null,
                sourceUrl = null,
                note = diaryRecipe.note,
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
                    measurement = ingredient.measurement.type,
                    quantity = ingredient.measurement.rawValue,
                )

            measurementDao.insertDiaryRecipeIngredient(ingredientEntity)
        }

        return recipeId
    }

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
                                            Measurement.from(
                                                ingredient.measurement,
                                                ingredient.quantity,
                                            ) to it
                                        }
                                    }
                                        ?: ingredient.ingredientRecipeId?.let { recipeId ->
                                            observeRecipe(recipeId).filterNotNull().map {
                                                Measurement.from(
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
                        note = entity.note,
                    )
                }
            }
            .flatMapLatest { it }

    private suspend fun deleteProduct(productId: Long) {
        val product = measurementDao.observeDiaryProduct(productId).first()
        if (product == null) {
            error("Diary product with id $productId not found")
        }
        measurementDao.deleteDiaryProduct(product)
    }

    private suspend fun deleteRecipe(recipeId: Long) {
        val recipe = measurementDao.observeDiaryRecipe(recipeId).first()
        if (recipe == null) {
            error("Diary recipe with id $recipeId not found")
        }
        measurementDao.deleteDiaryRecipe(recipe)
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
        isLiquid = isLiquid,
        sourceType = source.type.toEntity(),
        sourceUrl = source.url,
        note = note,
    )
}

private fun DiaryProductEntity.toModel(): DiaryFoodProduct =
    DiaryFoodProduct(
        name = name,
        nutritionFacts = toNutritionFacts(nutrients, vitamins, minerals),
        servingWeight = servingWeight,
        totalWeight = packageWeight,
        isLiquid = isLiquid,
        source = FoodSource(type = sourceType.toDomain(), url = sourceUrl),
        note = note,
    )
