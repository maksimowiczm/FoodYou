package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodProduct
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipeIngredient
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.domain.measurement.from
import com.maksimowiczm.foodyou.business.shared.domain.measurement.rawValue
import com.maksimowiczm.foodyou.business.shared.domain.measurement.type
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.FoodYouDatabase
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.DiaryProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.DiaryRecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.DiaryRecipeIngredientEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.MeasurementDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.MeasurementEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.immediateTransaction
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.toDomain
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.toEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.toEntityNutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.toNutritionFacts
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
internal class RoomFoodDiaryEntryRepository(private val database: FoodYouDatabase) :
    FoodDiaryEntryRepository {
    private val dao: MeasurementDao = database.measurementDao

    override fun observe(id: FoodDiaryEntryId): Flow<FoodDiaryEntry?> {
        return dao.observeMeasurementById(id.value).filterNotNull().flatMapLatest { entity ->
            observeFood(entity).map { food ->
                val createdAt =
                    Instant.fromEpochSeconds(entity.createdAt)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                val updatedAt =
                    Instant.fromEpochSeconds(entity.updatedAt)
                        .toLocalDateTime(TimeZone.currentSystemDefault())

                FoodDiaryEntry(
                    id = entity.id.toFoodDiaryEntryId(),
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

    override fun observeAll(mealId: Long, date: LocalDate): Flow<List<FoodDiaryEntry>> {
        return dao.observeMeasurements(mealId = mealId, epochDay = date.toEpochDays())
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
                            FoodDiaryEntry(
                                id = entity.id.toFoodDiaryEntryId(),
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
    }

    override suspend fun insert(
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
        food: DiaryFood,
        createdAt: LocalDateTime,
    ): FoodDiaryEntryId =
        database.immediateTransaction {
            val recipeId = run {
                if (food is DiaryFoodRecipe) {
                    insertRecipe(food)
                } else {
                    null
                }
            }

            val productId = run {
                if (food is DiaryFoodProduct) {
                    insertProduct(food)
                } else {
                    null
                }
            }

            if (recipeId == null && productId == null) {
                error("Diary entry must have either a product or a recipe")
            }

            val createdAt = createdAt.toInstant(TimeZone.currentSystemDefault()).epochSeconds

            val entity =
                MeasurementEntity(
                    mealId = mealId,
                    epochDay = date.toEpochDays(),
                    productId = productId,
                    recipeId = recipeId,
                    measurement = measurement.type,
                    quantity = measurement.rawValue,
                    createdAt = createdAt,
                    updatedAt = createdAt,
                )

            dao.insertMeasurement(entity).toFoodDiaryEntryId()
        }

    override suspend fun update(entry: FoodDiaryEntry) =
        database.immediateTransaction {
            val entity = dao.observeMeasurementById(entry.id.value).firstOrNull()

            if (entity == null) {
                error("Measurement with id ${entry.id} not found")
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
                if (entry.food is DiaryFoodProduct) {
                    insertProduct(entry.food)
                } else {
                    null
                }
            }
            val newRecipeId = run {
                if (entry.food is DiaryFoodRecipe) {
                    insertRecipe(entry.food)
                } else {
                    null
                }
            }

            if (newProductId == null && newRecipeId == null) {
                error("Diary entry must have either a product or a recipe")
            }

            val updatedEntity =
                entity.copy(
                    mealId = entry.mealId,
                    epochDay = entry.date.toEpochDays(),
                    productId = newProductId,
                    recipeId = newRecipeId,
                    measurement = entry.measurement.type,
                    quantity = entry.measurement.rawValue,
                    updatedAt =
                        entry.updatedAt.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
                )

            dao.updateMeasurement(updatedEntity)
        }

    override suspend fun delete(id: FoodDiaryEntryId) = dao.deleteMeasurement(id.value)

    private fun observeFood(measurementEntity: MeasurementEntity): Flow<DiaryFood> =
        measurementEntity.productId?.let { productId -> observeProduct(productId) }
            ?: measurementEntity.recipeId?.let { recipeId -> observeRecipe(recipeId) }
            ?: error("Measurement entity must have either a product or a recipe")

    private fun observeProduct(productId: Long): Flow<DiaryFoodProduct> =
        dao.observeDiaryProduct(productId).filterNotNull().map { entity -> entity.toModel() }

    private fun observeRecipe(recipeId: Long): Flow<DiaryFoodRecipe> =
        dao.observeDiaryRecipe(recipeId)
            .filterNotNull()
            .map { entity ->
                val ingredients =
                    dao.observeDiaryRecipeIngredients(entity.id)
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

    private suspend fun insertProduct(diaryFoodProduct: DiaryFoodProduct): Long {
        val entity = diaryFoodProduct.toEntity()
        return dao.insertDiaryProduct(entity)
    }

    private suspend fun insertRecipe(diaryRecipe: DiaryFoodRecipe): Long {

        val recipe =
            DiaryRecipeEntity(
                name = diaryRecipe.name,
                servings = diaryRecipe.servings,
                isLiquid = diaryRecipe.isLiquid,
                note = diaryRecipe.note,
            )

        val recipeId = dao.insertDiaryRecipe(recipe)

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

            dao.insertDiaryRecipeIngredient(ingredientEntity)
        }

        return recipeId
    }

    private suspend fun deleteProduct(productId: Long) = dao.deleteDiaryProduct(productId)

    private suspend fun deleteRecipe(recipeId: Long) = dao.deleteDiaryRecipe(recipeId)
}

private fun Long.toFoodDiaryEntryId(): FoodDiaryEntryId = FoodDiaryEntryId(this)

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
