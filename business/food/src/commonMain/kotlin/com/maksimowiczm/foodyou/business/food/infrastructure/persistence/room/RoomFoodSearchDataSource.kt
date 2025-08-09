package com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.business.shared.infrastructure.network.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodSearch as FoodSearchData
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodSearchDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.SearchEntry
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalPagingApi::class)
internal class RoomFoodSearchDataSource(private val foodSearchDao: FoodSearchDao) :
    LocalFoodSearchDataSource {
    override fun search(
        query: String?,
        source: FoodSource.Type,
        config: PagingConfig,
        remoteMediatorFactory: RemoteMediatorFactory?,
        excludedRecipeId: Long?,
    ): Flow<PagingData<FoodSearch>> =
        Pager(
                config = config,
                pagingSourceFactory = {
                    foodSearchDao.observeFoodByQuery(
                        query = query,
                        source = source.toEntity(),
                        excludedRecipeId = excludedRecipeId,
                    )
                },
                remoteMediator = remoteMediatorFactory?.create(),
            )
            .flow
            .map { data -> data.map { it.toModel() } }

    override fun observeSearchHistory(limit: Int): Flow<List<SearchHistory>> =
        foodSearchDao.observeRecentSearches(limit).mapValues { it.toModel() }

    override suspend fun insertSearchHistory(entry: SearchHistory) {
        foodSearchDao.insertSearchEntry(entry.toEntity())
    }

    override fun observeFoodCount(
        query: String?,
        source: FoodSource.Type,
        excludedRecipeId: Long?,
    ): Flow<Int> =
        foodSearchDao.observeFoodCountByQuery(
            query = query,
            source = source.toEntity(),
            excludedRecipeId = excludedRecipeId,
        )
}

private fun FoodSearchData.toModel(): FoodSearch =
    when (foodId) {
        is FoodId.Product ->
            FoodSearch.Product(
                id = foodId as FoodId.Product,
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
                defaultMeasurement = defaultMeasurement,
            )

        is FoodId.Recipe ->
            FoodSearch.Recipe(
                id = foodId as FoodId.Recipe,
                headline = headline,
                isLiquid = isLiquid,
                defaultMeasurement = Measurement.Serving(1.0),
            )
    }

private val FoodSearchData.foodId: FoodId
    get() =
        productId?.let(FoodId::Product)
            ?: recipeId?.let(FoodId::Recipe)
            ?: error("Food must have either productId or recipeId")

private val FoodSearchData.defaultMeasurement
    get() =
        when {
            servingWeight != null -> Measurement.Serving(1.0)
            totalWeight != null -> Measurement.Package(1.0)
            isLiquid -> Measurement.Milliliter(100.0)
            else -> Measurement.Gram(100.0)
        }

@OptIn(ExperimentalTime::class)
private fun SearchEntry.toModel(): SearchHistory =
    SearchHistory(
        date =
            epochSeconds
                .let(Instant::fromEpochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        query = query,
    )

@OptIn(ExperimentalTime::class)
private fun SearchHistory.toEntity(): SearchEntry =
    SearchEntry(
        epochSeconds = date.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
        query = query,
    )
