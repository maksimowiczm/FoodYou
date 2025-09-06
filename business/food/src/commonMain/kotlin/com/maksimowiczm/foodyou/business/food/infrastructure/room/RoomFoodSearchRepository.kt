package com.maksimowiczm.foodyou.business.food.infrastructure.room

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.food.FoodSearch as FoodSearchData
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.food.FoodSearchDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.shared.toEntity
import com.maksimowiczm.foodyou.core.food.domain.RemoteMediatorFactory
import com.maksimowiczm.foodyou.core.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.core.food.domain.entity.FoodSearch
import com.maksimowiczm.foodyou.core.food.domain.repository.FoodSearchRepository
import com.maksimowiczm.foodyou.core.shared.food.FoodSource
import com.maksimowiczm.foodyou.core.shared.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.shared.food.NutritionFacts
import com.maksimowiczm.foodyou.core.shared.measurement.Measurement
import com.maksimowiczm.foodyou.core.shared.measurement.from
import com.maksimowiczm.foodyou.core.shared.search.SearchQuery
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class, ExperimentalTime::class)
internal class RoomFoodSearchRepository(private val foodSearchDao: FoodSearchDao) :
    FoodSearchRepository {
    override fun search(
        query: SearchQuery,
        source: FoodSource.Type,
        config: PagingConfig,
        remoteMediatorFactory: RemoteMediatorFactory?,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>> =
        Pager(
                config = config,
                pagingSourceFactory = {
                    when (query) {
                        SearchQuery.Blank,
                        is SearchQuery.Text ->
                            foodSearchDao.observeFoodByQuery(
                                query = query.query,
                                source = source.toEntity(),
                                excludedRecipeId = excludedRecipeId?.id,
                            )

                        is SearchQuery.Barcode ->
                            foodSearchDao.observeFoodByBarcode(
                                barcode = query.query,
                                source = source.toEntity(),
                            )
                    }
                },
                remoteMediator = remoteMediatorFactory?.create(),
            )
            .flow
            .map { data -> data.map { it.toModel() } }

    override fun searchRecent(
        query: SearchQuery,
        config: PagingConfig,
        now: LocalDateTime,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>> =
        Pager(
                config = config,
                pagingSourceFactory = {
                    val nowEpochSeconds =
                        now.toInstant(TimeZone.currentSystemDefault()).epochSeconds

                    when (query) {
                        SearchQuery.Blank,
                        is SearchQuery.Text ->
                            foodSearchDao.observeRecentFoodByQuery(
                                query = query.query,
                                nowEpochSeconds = nowEpochSeconds,
                                excludedRecipeId = excludedRecipeId?.id,
                            )

                        is SearchQuery.Barcode ->
                            foodSearchDao.observeRecentFoodByBarcode(
                                barcode = query.query,
                                nowEpochSeconds = nowEpochSeconds,
                            )
                    }
                },
            )
            .flow
            .map { data -> data.map { it.toModel() } }

    override fun searchFoodCount(
        query: SearchQuery,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int> =
        when (query) {
            SearchQuery.Blank,
            is SearchQuery.Text ->
                foodSearchDao.observeFoodCountByQuery(
                    query = query.query,
                    source = source.toEntity(),
                    excludedRecipeId = excludedRecipeId?.id,
                )

            is SearchQuery.Barcode ->
                foodSearchDao.observeFoodCountByBarcode(
                    barcode = query.query,
                    source = source.toEntity(),
                )
        }

    override fun searchRecentFoodCount(
        query: SearchQuery,
        now: LocalDateTime,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int> =
        when (query) {
            SearchQuery.Blank,
            is SearchQuery.Text ->
                foodSearchDao.observeRecentFoodCountByQuery(
                    query = query.query,
                    nowEpochSeconds = now.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
                    excludedRecipeId = excludedRecipeId?.id,
                )

            is SearchQuery.Barcode ->
                foodSearchDao.observeRecentFoodCountByBarcode(
                    barcode = query.query,
                    nowEpochSeconds = now.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
                )
        }
}

private fun FoodSearchData.toModel(): FoodSearch =
    when (val foodId = foodId) {
        is FoodId.Product ->
            FoodSearch.Product(
                id = foodId,
                headline = headline,
                isLiquid = isLiquid,
                nutritionFacts =
                    NutritionFacts(
                        proteins = nutrients?.proteins.toNutrientValue(),
                        carbohydrates = nutrients?.carbohydrates.toNutrientValue(),
                        energy = nutrients?.energy.toNutrientValue(),
                        fats = nutrients?.fats.toNutrientValue(),
                        saturatedFats = nutrients?.saturatedFats.toNutrientValue(),
                        transFats = nutrients?.transFats.toNutrientValue(),
                        monounsaturatedFats = nutrients?.monounsaturatedFats.toNutrientValue(),
                        polyunsaturatedFats = nutrients?.polyunsaturatedFats.toNutrientValue(),
                        omega3 = nutrients?.omega3.toNutrientValue(),
                        omega6 = nutrients?.omega6.toNutrientValue(),
                        sugars = nutrients?.sugars.toNutrientValue(),
                        addedSugars = nutrients?.addedSugars.toNutrientValue(),
                        dietaryFiber = nutrients?.dietaryFiber.toNutrientValue(),
                        solubleFiber = nutrients?.solubleFiber.toNutrientValue(),
                        insolubleFiber = nutrients?.insolubleFiber.toNutrientValue(),
                        salt = nutrients?.salt.toNutrientValue(),
                        cholesterol = nutrients?.cholesterolMilli.toNutrientValue() / 1_000.0,
                        caffeine = nutrients?.caffeineMilli.toNutrientValue() / 1_000.0,
                        vitaminA = vitamins?.vitaminAMicro.toNutrientValue() / 1_000_000.0,
                        vitaminB1 = vitamins?.vitaminB1Milli.toNutrientValue() / 1_000.0,
                        vitaminB2 = vitamins?.vitaminB2Milli.toNutrientValue() / 1_000.0,
                        vitaminB3 = vitamins?.vitaminB3Milli.toNutrientValue() / 1_000.0,
                        vitaminB5 = vitamins?.vitaminB5Milli.toNutrientValue() / 1_000.0,
                        vitaminB6 = vitamins?.vitaminB6Milli.toNutrientValue() / 1_000.0,
                        vitaminB7 = vitamins?.vitaminB7Micro.toNutrientValue() / 1_000_000.0,
                        vitaminB9 = vitamins?.vitaminB9Micro.toNutrientValue() / 1_000_000.0,
                        vitaminB12 = vitamins?.vitaminB12Micro.toNutrientValue() / 1_000_000.0,
                        vitaminC = vitamins?.vitaminCMilli.toNutrientValue() / 1_000.0,
                        vitaminD = vitamins?.vitaminDMicro.toNutrientValue() / 1_000_000.0,
                        vitaminE = vitamins?.vitaminEMilli.toNutrientValue() / 1_000.0,
                        vitaminK = vitamins?.vitaminKMicro.toNutrientValue() / 1_000_000.0,
                        manganese = minerals?.manganeseMilli.toNutrientValue() / 1_000.0,
                        magnesium = minerals?.magnesiumMilli.toNutrientValue() / 1_000.0,
                        potassium = minerals?.potassiumMilli.toNutrientValue() / 1_000.0,
                        calcium = minerals?.calciumMilli.toNutrientValue() / 1_000.0,
                        copper = minerals?.copperMilli.toNutrientValue() / 1_000.0,
                        zinc = minerals?.zincMilli.toNutrientValue() / 1_000.0,
                        sodium = minerals?.sodiumMilli.toNutrientValue() / 1_000.0,
                        iron = minerals?.ironMilli.toNutrientValue() / 1_000.0,
                        phosphorus = minerals?.phosphorusMilli.toNutrientValue() / 1_000.0,
                        selenium = minerals?.seleniumMicro.toNutrientValue() / 1_000_000.0,
                        iodine = minerals?.iodineMicro.toNutrientValue() / 1_000_000.0,
                        chromium = minerals?.chromiumMicro.toNutrientValue() / 1_000_000.0,
                    ),
                totalWeight = totalWeight,
                servingWeight = servingWeight,
                suggestedMeasurement = suggestedMeasurement,
            )

        is FoodId.Recipe ->
            FoodSearch.Recipe(
                id = foodId,
                headline = headline,
                isLiquid = isLiquid,
                suggestedMeasurement = suggestedMeasurement,
            )
    }

private val FoodSearchData.foodId: FoodId
    get() =
        productId?.let(FoodId::Product)
            ?: recipeId?.let(FoodId::Recipe)
            ?: error("Food must have either productId or recipeId")

private val FoodSearchData.suggestedMeasurement
    get() =
        when {
            measurementType != null && measurementValue != null ->
                Measurement.from(measurementType!!, measurementValue!!.toDouble())

            recipeId != null || servingWeight != null -> Measurement.Serving(1.0)
            totalWeight != null -> Measurement.Package(1.0)
            isLiquid -> Measurement.Milliliter(100.0)
            else -> Measurement.Gram(100.0)
        }
