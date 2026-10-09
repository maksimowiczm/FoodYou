package com.maksimowiczm.foodyou.foodyou3.infrastructure.food

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.foodyou3.domain.FoodYou3FoodSource
import com.maksimowiczm.foodyou.foodyou3.domain.FoodYou3Measurement
import com.maksimowiczm.foodyou.foodyou3.domain.food.FoodYou3FoodId
import com.maksimowiczm.foodyou.foodyou3.domain.food.FoodYou3FoodSearch
import com.maksimowiczm.foodyou.foodyou3.domain.food.FoodYou3FoodSearchRepository
import com.maksimowiczm.foodyou.foodyou3.domain.from
import com.maksimowiczm.foodyou.foodyou3.infrastructure.food.FoodYou3FoodSearch as FoodYou3InfraFoodSearch
import com.maksimowiczm.foodyou.foodyou3.infrastructure.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class FoodYou3SqliteFoodSearchRepository(
    private val foodSearchDao: SqliteFoodYou3FoodSearchDao
) : FoodYou3FoodSearchRepository {
    override fun search(
        query: SearchQuery,
        source: FoodYou3FoodSource.Type?,
        config: PagingConfig,
    ): Flow<PagingData<FoodYou3FoodSearch>> =
        Pager(
                config = config,
                pagingSourceFactory = {
                    val entitySource = source?.toEntity()
                    when (query) {
                        SearchQuery.Blank ->
                            SqlitePagingSource { limit, offset ->
                                foodSearchDao.getFood(
                                    source = entitySource,
                                    limit = limit,
                                    offset = offset,
                                )
                            }

                        is SearchQuery.Text ->
                            SqlitePagingSource { limit, offset ->
                                foodSearchDao.getFoodByQuery(
                                    query = query.query,
                                    source = entitySource,
                                    limit = limit,
                                    offset = offset,
                                )
                            }

                        is SearchQuery.Barcode ->
                            SqlitePagingSource { limit, offset ->
                                foodSearchDao.getFoodByBarcode(
                                    barcode = query.barcode,
                                    source = entitySource,
                                    limit = limit,
                                    offset = offset,
                                )
                            }

                        is SearchQuery.NotBlank ->
                            SqlitePagingSource { limit, offset ->
                                foodSearchDao.getFoodByQuery(
                                    query = query.query,
                                    source = entitySource,
                                    limit = limit,
                                    offset = offset,
                                )
                            }
                    }
                },
            )
            .flow
            .map { pagingData: PagingData<FoodYou3InfraFoodSearch> ->
                pagingData.map { infraSearch -> infraSearch.toModel() }
            }

    override fun searchFoodCount(
        query: SearchQuery,
        source: FoodYou3FoodSource.Type?,
    ): Flow<Int> = flow {
        val entitySource = source?.toEntity()
        val count =
            when (query) {
                SearchQuery.Blank -> foodSearchDao.getFoodCount(source = entitySource)

                is SearchQuery.Text ->
                    foodSearchDao.getFoodCountByQuery(
                        query = query.query,
                        source = entitySource,
                    )

                is SearchQuery.Barcode ->
                    foodSearchDao.getFoodCountByBarcode(
                        barcode = query.barcode,
                        source = entitySource,
                    )

                is SearchQuery.NotBlank ->
                    foodSearchDao.getFoodCountByQuery(
                        query = query.query,
                        source = entitySource,
                    )
            }
        emit(count)
    }
}

private fun FoodYou3InfraFoodSearch.toModel(): FoodYou3FoodSearch =
    when (val foodId = foodId) {
        is FoodYou3FoodId.Product ->
            FoodYou3FoodSearch.Product(
                id = foodId,
                headline = headline,
                isLiquid = isLiquid,
                nutritionFacts =
                    NutritionFacts.requireAll(
                        proteins = nutrients?.proteins?.grams.toNutrientValue(),
                        carbohydrates = nutrients?.carbohydrates?.grams.toNutrientValue(),
                        energy = nutrients?.energy?.kilocalories.toNutrientValue(),
                        fats = nutrients?.fats?.grams.toNutrientValue(),
                        saturatedFats = nutrients?.saturatedFats?.grams.toNutrientValue(),
                        transFats = nutrients?.transFats?.grams.toNutrientValue(),
                        monounsaturatedFats =
                            nutrients?.monounsaturatedFats?.grams.toNutrientValue(),
                        polyunsaturatedFats =
                            nutrients?.polyunsaturatedFats?.grams.toNutrientValue(),
                        omega3 = nutrients?.omega3?.grams.toNutrientValue(),
                        omega6 = nutrients?.omega6?.grams.toNutrientValue(),
                        sugars = nutrients?.sugars?.grams.toNutrientValue(),
                        addedSugars = nutrients?.addedSugars?.grams.toNutrientValue(),
                        dietaryFiber = nutrients?.dietaryFiber?.grams.toNutrientValue(),
                        solubleFiber = nutrients?.solubleFiber?.grams.toNutrientValue(),
                        insolubleFiber = nutrients?.insolubleFiber?.grams.toNutrientValue(),
                        salt = nutrients?.salt?.grams.toNutrientValue(),
                        cholesterol = nutrients?.cholesterolMilli?.milligrams.toNutrientValue(),
                        caffeine = nutrients?.caffeineMilli?.milligrams.toNutrientValue(),
                        vitaminA = vitamins?.vitaminAMicro?.micrograms.toNutrientValue(),
                        vitaminB1 = vitamins?.vitaminB1Milli?.milligrams.toNutrientValue(),
                        vitaminB2 = vitamins?.vitaminB2Milli?.milligrams.toNutrientValue(),
                        vitaminB3 = vitamins?.vitaminB3Milli?.milligrams.toNutrientValue(),
                        vitaminB5 = vitamins?.vitaminB5Milli?.milligrams.toNutrientValue(),
                        vitaminB6 = vitamins?.vitaminB6Milli?.milligrams.toNutrientValue(),
                        vitaminB7 = vitamins?.vitaminB7Micro?.micrograms.toNutrientValue(),
                        vitaminB9 = vitamins?.vitaminB9Micro?.micrograms.toNutrientValue(),
                        vitaminB12 = vitamins?.vitaminB12Micro?.micrograms.toNutrientValue(),
                        vitaminC = vitamins?.vitaminCMilli?.milligrams.toNutrientValue(),
                        vitaminD = vitamins?.vitaminDMicro?.micrograms.toNutrientValue(),
                        vitaminE = vitamins?.vitaminEMilli?.milligrams.toNutrientValue(),
                        vitaminK = vitamins?.vitaminKMicro?.micrograms.toNutrientValue(),
                        manganese = minerals?.manganeseMilli?.milligrams.toNutrientValue(),
                        magnesium = minerals?.magnesiumMilli?.milligrams.toNutrientValue(),
                        potassium = minerals?.potassiumMilli?.milligrams.toNutrientValue(),
                        calcium = minerals?.calciumMilli?.milligrams.toNutrientValue(),
                        copper = minerals?.copperMilli?.milligrams.toNutrientValue(),
                        zinc = minerals?.zincMilli?.milligrams.toNutrientValue(),
                        sodium = minerals?.sodiumMilli?.milligrams.toNutrientValue(),
                        iron = minerals?.ironMilli?.milligrams.toNutrientValue(),
                        phosphorus = minerals?.phosphorusMilli?.milligrams.toNutrientValue(),
                        selenium = minerals?.seleniumMicro?.micrograms.toNutrientValue(),
                        iodine = minerals?.iodineMicro?.micrograms.toNutrientValue(),
                        chromium = minerals?.chromiumMicro?.micrograms.toNutrientValue(),
                    ),
                totalWeight = totalWeight,
                servingWeight = servingWeight,
                suggestedMeasurement = suggestedMeasurement,
            )

        is FoodYou3FoodId.Recipe ->
            FoodYou3FoodSearch.Recipe(
                id = foodId,
                headline = headline,
                isLiquid = isLiquid,
                suggestedMeasurement = suggestedMeasurement,
            )
    }

private val FoodYou3InfraFoodSearch.foodId: FoodYou3FoodId
    get() =
        productId?.let(FoodYou3FoodId::Product)
            ?: recipeId?.let(FoodYou3FoodId::Recipe)
            ?: error("Food must have either productId or recipeId")

private val FoodYou3InfraFoodSearch.suggestedMeasurement
    get() =
        when {
            measurementType != null && measurementValue != null ->
                FoodYou3Measurement.from(measurementType, measurementValue)

            recipeId != null || servingWeight != null -> FoodYou3Measurement.Serving(1.0)
            totalWeight != null -> FoodYou3Measurement.Package(1.0)
            isLiquid -> FoodYou3Measurement.Milliliter(100.0)
            else -> FoodYou3Measurement.Gram(100.0)
        }
