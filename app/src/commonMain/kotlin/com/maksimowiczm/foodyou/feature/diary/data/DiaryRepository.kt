package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryMeasuredProduct
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.SearchModel
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.data.model.defaultGoals
import com.maksimowiczm.foodyou.feature.diary.data.model.toDomain
import com.maksimowiczm.foodyou.feature.diary.data.model.toEntity
import com.maksimowiczm.foodyou.feature.diary.data.preferences.DiaryPreferences
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.diary.database.measurement.ProductWithWeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.database.measurement.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.database.search.SearchEntity
import com.maksimowiczm.foodyou.feature.diary.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlin.collections.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

// TODO Make it not a god class
class DiaryRepository(
    database: DiaryDatabase,
    private val productRemoteMediatorFactory: ProductRemoteMediatorFactory,
    private val dataStore: DataStore<Preferences>,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : MealRepository,
    GoalsRepository,
    DiaryDayRepository,
    SearchRepository {

    private val searchDao = database.searchDao
    private val addFoodDao = database.addFoodDao()

    override fun observeDailyGoals(): Flow<DailyGoals> {
        val nutrientGoal = combine(
            dataStore.observe(DiaryPreferences.proteinsGoal),
            dataStore.observe(DiaryPreferences.carbohydratesGoal),
            dataStore.observe(DiaryPreferences.fatsGoal)
        ) { arr ->
            if (arr.any { it == null }) {
                return@combine null
            }

            arr.map { it!! }
        }

        return combine(
            dataStore.observe(DiaryPreferences.caloriesGoal),
            nutrientGoal
        ) { calories, nutrients ->
            if (nutrients == null || calories == null) {
                return@combine defaultGoals()
            }

            val (proteins, carbohydrates, fats) = nutrients

            DailyGoals(
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats
            )
        }
    }

    override fun observeDiaryDay(date: LocalDate): Flow<DiaryDay> {
        val epochDay = date.toEpochDays()

        return combine(
            addFoodDao.observeMeasuredProducts(
                mealId = null,
                epochDay = epochDay
            ),
            observeMeals(),
            observeDailyGoals()
        ) { products, meals, goals ->
            val mealProductMap = meals
                .associateWith { emptyList<DiaryMeasuredProduct>() }
                .toMutableMap()

            products.forEach {
                val product = it.toMeasurement()
                val mealId = it.weightMeasurement.mealId
                val key = meals.firstOrNull { meal -> meal.id == mealId }

                if (key == null) {
                    Logger.e(TAG) { "Meal with id $mealId not found. Data inconsistency. BYE BYE" }
                    error("Meal with id $mealId not found. Data inconsistency. BYE BYE")
                }

                mealProductMap[key] = mealProductMap[key]!! + product
            }

            return@combine DiaryDay(
                date = date,
                mealProductMap = mealProductMap,
                dailyGoals = goals
            )
        }
    }

    override suspend fun setDailyGoals(goals: DailyGoals) {
        dataStore.set(
            DiaryPreferences.caloriesGoal to goals.calories,
            DiaryPreferences.proteinsGoal to goals.proteins,
            DiaryPreferences.carbohydratesGoal to goals.carbohydrates,
            DiaryPreferences.fatsGoal to goals.fats
        )
    }

    override fun observeMeals(): Flow<List<Meal>> = addFoodDao.observeMeals().map { list ->
        list.map(MealEntity::toDomain)
    }

    override fun observeMealById(id: Long) = addFoodDao.observeMealById(id).map { it?.toDomain() }

    override suspend fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        addFoodDao.insertWithLastRank(
            MealEntity(
                name = name,
                fromHour = from.hour,
                fromMinute = from.minute,
                toHour = to.hour,
                toMinute = to.minute,
                rank = -1
            )
        )
    }

    override suspend fun updateMeal(meal: Meal) {
        addFoodDao.updateMeal(meal.toEntity())
    }

    override suspend fun deleteMeal(meal: Meal) {
        addFoodDao.deleteMeal(meal.toEntity())
    }

    override suspend fun updateMealsRanks(map: Map<Long, Int>) {
        addFoodDao.updateMealsRanks(map)
    }

    override fun observeProductQueries(limit: Int): Flow<List<ProductQuery>> =
        addFoodDao.observeLatestQueries(limit).map { list ->
            list.map { it.toDomain() }
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun queryProducts(query: String?): Flow<PagingData<SearchModel>> {
        val barcode = query?.takeIf { it.all(Char::isDigit) }

        val localOnly = query == null
        val remoteMediator: RemoteMediator<Int, SearchEntity>? = when {
            localOnly -> null
            barcode != null -> productRemoteMediatorFactory.createWithBarcode(barcode)
            else -> productRemoteMediatorFactory.createWithQuery(query)
        }

        // Insert query if it's not a barcode and not empty
        if (barcode == null && query?.isNotBlank() == true) {
            ioScope.launch {
                insertProductQueryWithCurrentTime(query)
            }
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE
            ),
            remoteMediator = remoteMediator
        ) {
            searchDao.queryFood(query)
        }.flow.map { pagingData ->
            pagingData.map {
                it.toSearchModel()
            }
        }
    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val epochSeconds = Clock.System.now().epochSeconds

        addFoodDao.upsertProductQuery(
            ProductQueryEntity(
                query = query,
                date = epochSeconds
            )
        )
    }

    companion object {
        private const val TAG = "DiaryRepository"
        private const val PAGE_SIZE = 30
    }
}

private fun ProductWithWeightMeasurementEntity.toMeasurement(): DiaryMeasuredProduct =
    DiaryMeasuredProduct(
        product = product.toDomain(),
        measurement = weightMeasurement.toDomain(),
        measurementId = MeasurementId.Product(this.weightMeasurement.id)
    )

private fun WeightMeasurementEntity.toDomain() = when (this.measurement) {
    WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(quantity)
    WeightMeasurementEnum.Package -> WeightMeasurement.Package(quantity)
    WeightMeasurementEnum.Serving -> WeightMeasurement.Serving(quantity)
}

private fun SearchEntity.toSearchModel(): SearchModel {
    val weightMeasurement = when {
        this@toSearchModel.measurement == null || quantity == null -> {
            when {
                servingWeight != null -> WeightMeasurement.Serving(1f)
                packageWeight != null -> WeightMeasurement.Package(1f)
                else -> WeightMeasurement.WeightUnit(100f)
            }
        }

        else -> when (this@toSearchModel.measurement) {
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

        SearchModel(
            foodId = FoodId.Product(productId),
            name = name,
            brand = brand,
            calories = calories,
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            measurement = weightMeasurement
        )
    } else if (recipeId != null) {
        if (packageWeight == null) {
            error("Package weight should not be null for recipe measurement")
        }
        if (servings == null) {
            error("Servings should not be null for recipe measurement")
        }

        val servingWeight = packageWeight / servings

        SearchModel(
            foodId = FoodId.Recipe(recipeId),
            name = name,
            brand = brand,
            calories = calories,
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            measurement = weightMeasurement
        )
    } else {
        error("Database corruption for search entity: $this")
    }
}
