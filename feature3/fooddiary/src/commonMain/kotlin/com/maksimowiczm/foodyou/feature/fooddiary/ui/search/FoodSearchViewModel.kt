package com.maksimowiczm.foodyou.feature.fooddiary.ui.search

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.Meal
import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Food
import com.maksimowiczm.foodyou.feature.fooddiary.domain.FoodMapper
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain.ObserveOpenFoodFactsProductCountUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain.ObserveOpenFoodFactsProductPagesUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.MeasurementMapper
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal class FoodSearchViewModel(
    mealId: Long,
    date: LocalDate,
    database: FoodDiaryDatabase,
    dataStore: DataStore<Preferences>,
    private val measurementMapper: MeasurementMapper,
    private val foodMapper: FoodMapper,
    private val observeOpenFoodFactsProduct: ObserveOpenFoodFactsProductPagesUseCase,
    private val observeOpenFoodFactsProductCount: ObserveOpenFoodFactsProductCountUseCase
) : ViewModel() {

    private val foodDao = database.foodDao
    private val mealDao = database.mealDao
    private val measurementDao = database.measurementDao
    private val useOpenFoodFacts = dataStore.userPreference<UseOpenFoodFacts>()

    val meal = mealDao.observeMealById(mealId).stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = WhileSubscribed(2_000)
    )

    val date = MutableStateFlow(date).asStateFlow()

    private val searchQuery = MutableStateFlow<String?>(null)

    /**
     * Triggers a search for food items based on the provided query.
     *
     * @param query The search query string. If null, it will reset the search.
     */
    fun search(query: String?) {
        searchQuery.value = query
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val localPages = searchQuery.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { foodDao.observeFood(query) }
        ).flow.map { data ->
            data.map(foodMapper::toFood)
        }.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val openFoodFactsPages = useOpenFoodFacts.observe().filter { it }.flatMapLatest {
        searchQuery.flatMapLatest {
            observeOpenFoodFactsProduct(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val openFoodFactsProductCount = useOpenFoodFacts.observe().filter { it }.flatMapLatest {
        searchQuery.flatMapLatest {
            observeOpenFoodFactsProductCount(it)
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = 0,
        started = WhileSubscribed(2_000)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val measurements = combine(
        meal.filterNotNull(),
        this.date
    ) { meal, date -> meal to date }.flatMapLatest { (meal, date) ->
        measurementDao.observeMeasurements(
            mealId = meal.id,
            epochDay = date.toEpochDays()
        ).map { list ->
            list.map(foodMapper::toFoodMeasurement)
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = WhileSubscribed(2_000)
    )

    @OptIn(ExperimentalTime::class)
    fun measureFood(food: Food, measurement: Measurement, meal: Meal, date: LocalDate) {
        val measurement = MeasurementEntity(
            mealId = meal.id,
            epochDay = date.toEpochDays(),
            productId = (food.id as? FoodId.Product)?.id,
            recipeId = (food.id as? FoodId.Recipe)?.id,
            measurement = measurementMapper.toEntity(measurement),
            quantity = measurementMapper.toQuantity(measurement),
            createdAt = Clock.System.now().epochSeconds
        )

        viewModelScope.launch {
            measurementDao.insertMeasurement(measurement)
        }
    }

    fun deleteMeasurement(measurementId: Long) {
        viewModelScope.launch {
            val entity = measurementDao.observeMeasurementById(measurementId).firstOrNull()
            if (entity == null) return@launch
            measurementDao.deleteMeasurement(entity)
        }
    }
}
