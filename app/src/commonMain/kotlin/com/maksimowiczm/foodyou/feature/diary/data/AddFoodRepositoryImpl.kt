package com.maksimowiczm.foodyou.feature.diary.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.data.model.toDomain
import com.maksimowiczm.foodyou.feature.diary.database.dao.AddFoodDao
import com.maksimowiczm.foodyou.feature.diary.database.dao.ProductDao
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductSearchEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.network.ProductRemoteMediator
import com.maksimowiczm.foodyou.feature.diary.network.ProductRemoteMediatorFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class AddFoodRepositoryImpl(
    private val addFoodDao: AddFoodDao,
    private val productDao: ProductDao,
    private val productRemoteMediatorFactory: ProductRemoteMediatorFactory,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : AddFoodRepository {

    override suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        productId: Long,
        weightMeasurement: WeightMeasurement
    ) {
        val quantity = when (weightMeasurement) {
            is WeightMeasurement.WeightUnit -> weightMeasurement.weight
            is WeightMeasurement.Package -> weightMeasurement.quantity
            is WeightMeasurement.Serving -> weightMeasurement.quantity
        }

        val epochSeconds = Clock.System.now().epochSeconds

        val entity = WeightMeasurementEntity(
            mealId = mealId,
            diaryEpochDay = date.toEpochDays(),
            productId = productId,
            measurement = weightMeasurement.asEnum(),
            quantity = quantity,
            createdAt = epochSeconds
        )

        return addFoodDao.insertWeightMeasurement(entity)
    }

    override suspend fun removeMeasurement(measurementId: Long) {
        val entity = addFoodDao.observeWeightMeasurement(
            measurementId = measurementId,
            isDeleted = false
        ).first()

        if (entity != null) {
            addFoodDao.deleteWeightMeasurement(entity.id)
        }
    }

    override suspend fun restoreMeasurement(measurementId: Long) {
        val entity = addFoodDao.observeWeightMeasurement(
            measurementId = measurementId,
            isDeleted = true
        ).first()

        if (entity != null) {
            addFoodDao.restoreWeightMeasurement(entity.id)
        }
    }

    override suspend fun updateMeasurement(
        measurementId: Long,
        weightMeasurement: WeightMeasurement
    ) {
        val entity = addFoodDao.observeWeightMeasurement(
            measurementId = measurementId,
            isDeleted = false
        ).first()

        if (entity == null) {
            Logger.w(TAG) { "Measurement not found for ID $measurementId." }
            return
        }

        val quantity = when (weightMeasurement) {
            is WeightMeasurement.WeightUnit -> weightMeasurement.weight
            is WeightMeasurement.Package -> weightMeasurement.quantity
            is WeightMeasurement.Serving -> weightMeasurement.quantity
        }

        val updatedEntity = entity.copy(
            measurement = weightMeasurement.asEnum(),
            quantity = quantity
        )

        addFoodDao.updateWeightMeasurement(updatedEntity)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<PagingData<ProductWithWeightMeasurement>> {
        val barcode = query?.takeIf { it.all(Char::isDigit) }

        val localOnly = query == null
        val remoteMediator = when {
            localOnly -> null
            barcode != null -> productRemoteMediatorFactory.createWithBarcode(barcode)
            else -> productRemoteMediatorFactory.createWithQuery(query)
        }?.let { ProductSearchRemoteMediatorAdapter(it) }

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
            if (barcode != null) {
                addFoodDao.observePagedProductsWithMeasurementByBarcode(
                    mealId = mealId,
                    date = date,
                    barcode = barcode
                )
            } else {
                addFoodDao.observePagedProductsWithMeasurementByQuery(
                    mealId = mealId,
                    date = date,
                    query = query
                )
            }
        }.flow.map { pagingData ->
            pagingData.map { it.toDomain() }
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

    override fun observeQuantitySuggestionByProductId(productId: Long) = combine(
        productDao.observeProductById(productId),
        addFoodDao.observeQuantitySuggestionsByProductId(productId)
    ) { product, suggestionList ->
        if (product == null) {
            Logger.w(TAG) {
                "Product not found for ID $productId. Skipping quantity suggestion."
            }
            return@combine null
        }

        val suggestions = suggestionList
            .associate { it.measurement to it.quantity }
            .toMutableMap()

        val default = QuantitySuggestion.defaultSuggestion
        WeightMeasurementEnum.entries.forEach {
            if (!suggestions.containsKey(it)) {
                suggestions[it] = default[it] ?: error("Default suggestion not found for $it")
            }
        }

        QuantitySuggestion(
            product = product.toDomain(),
            quantitySuggestions = suggestions
        )
    }.filterNotNull()

    override fun observeProductQueries(limit: Int): Flow<List<ProductQuery>> =
        addFoodDao.observeLatestQueries(limit).map { list ->
            list.map { it.toDomain() }
        }

    override fun observeProductByMeasurementId(measurementId: Long) =
        addFoodDao.observeProductByMeasurementId(measurementId).map { entity ->
            entity?.toDomain()
        }

    private companion object {
        private const val TAG = "AddFoodRepositoryImpl"
        private const val PAGE_SIZE = 30
    }
}

private fun AddFoodDao.observePagedProductsWithMeasurementByQuery(
    mealId: Long,
    date: LocalDate,
    query: String?
) = observePagedProductsWithMeasurement(
    mealId = mealId,
    epochDay = date.toEpochDays(),
    query = query,
    barcode = null
)

private fun AddFoodDao.observePagedProductsWithMeasurementByBarcode(
    mealId: Long,
    date: LocalDate,
    barcode: String
) = observePagedProductsWithMeasurement(
    mealId = mealId,
    epochDay = date.toEpochDays(),
    query = null,
    barcode = barcode
)

// Adapter for RemoteMediator<Int, ProductSearchEntity> to RemoteMediator<Int, ProductEntity>
// Got to love paging3 library :) (most likely skill issue from my side)
@OptIn(ExperimentalPagingApi::class)
private class ProductSearchRemoteMediatorAdapter(
    private val productRemoteMediator: ProductRemoteMediator
) : RemoteMediator<Int, ProductSearchEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductSearchEntity>
    ): MediatorResult {
        val pages: List<Page<Int, ProductEntity>> = state.pages.map { page ->
            Page(
                data = page.data.map { it.product },
                nextKey = page.nextKey,
                prevKey = page.prevKey
            )
        }

        return productRemoteMediator.load(
            loadType = loadType,
            state = PagingState(
                pages = pages,
                config = state.config,
                anchorPosition = state.anchorPosition,
                leadingPlaceholderCount = state.leadingPlaceholderCount
            )
        )
    }
}

private val PagingState<Int, ProductSearchEntity>.leadingPlaceholderCount: Int
    get() {
        val field = PagingState::class.java.getDeclaredField("leadingPlaceholderCount")
        field.isAccessible = true
        return field.get(this) as Int
    }
