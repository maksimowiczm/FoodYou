package com.maksimowiczm.foodyou.food.infrastructure.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.infrastructure.filekit.directory
import com.maksimowiczm.foodyou.food.domain.Barcode
import com.maksimowiczm.foodyou.food.domain.FoodBrand
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.domain.FoodNote
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.domain.FoodSource
import com.maksimowiczm.foodyou.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.food.domain.QueryParameters
import com.maksimowiczm.foodyou.food.domain.UserFoodRepository
import com.maksimowiczm.foodyou.food.infrastructure.user.room.UserFoodDao
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class UserFoodRepositoryImpl(
    private val dao: UserFoodDao,
    private val nameSelector: FoodNameSelector,
) : UserFoodRepository {
    private val mapper = UserFoodMapper()

    @OptIn(ExperimentalPagingApi::class)
    fun search(
        searchFoodParams: SearchParameters.User,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> {
        val language = nameSelector.select()

        when (searchFoodParams.query) {
            SearchQuery.Blank,
            is SearchQuery.Barcode,
            is SearchQuery.Text -> Unit

            is SearchQuery.FoodDataCentralUrl,
            is SearchQuery.OpenFoodFactsUrl -> {
                return flowOf(
                    PagingData.empty(
                        sourceLoadStates =
                            LoadStates(NotLoading(true), NotLoading(true), NotLoading(true))
                    )
                )
            }
        }

        val config = PagingConfig(pageSize = pageSize)
        val factory = {
            when (searchFoodParams.query) {
                SearchQuery.Blank ->
                    dao.getPagingSource(language.tag, searchFoodParams.accountId.value)

                is SearchQuery.Barcode ->
                    dao.getPagingSourceByBarcode(
                        searchFoodParams.query.barcode,
                        language.tag,
                        searchFoodParams.accountId.value,
                    )

                is SearchQuery.Text ->
                    dao.getPagingSourceByQuery(
                        searchFoodParams.query.query,
                        language.tag,
                        searchFoodParams.accountId.value,
                    )

                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> error("Unreachable")
            }
        }

        return Pager(config = config, pagingSourceFactory = factory).flow.map { data ->
            data.map(mapper::searchableFoodDto)
        }
    }

    fun count(parameters: SearchParameters.User): Flow<Int> {
        val countFlow: Flow<Int> =
            when (parameters.query) {
                SearchQuery.Blank -> dao.observeCount(parameters.accountId.value)

                is SearchQuery.Barcode ->
                    dao.observeCountByBarcode(parameters.query.barcode, parameters.accountId.value)

                is SearchQuery.Text ->
                    dao.observeCountByQuery(parameters.query.query, parameters.accountId.value)

                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> flowOf(0)
            }

        return countFlow
    }

    fun observe(queryParameters: QueryParameters.Local): Flow<FoodProductRepository.FoodStatus> {
        val (id, accountId) = queryParameters.identity

        return dao.observe(id, accountId.value)
            .map { entity -> entity?.let(mapper::foodProductDto) }
            .map {
                when (it) {
                    null -> FoodProductRepository.FoodStatus.NotFound(queryParameters.identity)
                    else -> FoodProductRepository.FoodStatus.Available(it)
                }
            }
    }

    override suspend fun create(
        name: FoodName,
        brand: FoodBrand?,
        barcode: Barcode?,
        note: FoodNote?,
        imageUri: String?,
        source: FoodSource.UserAdded?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    ): FoodProductIdentity.Local {
        val foodDirectory = accountId.directory() / "food"
        foodDirectory.createDirectories()

        val uuid = Uuid.random().toString()

        val photoPath =
            if (imageUri != null) {
                val sourceFile = PlatformFile(imageUri)
                require(sourceFile.exists()) { "Image file does not exist at path: $imageUri" }
                val bytes = sourceFile.readBytes()

                val compressed =
                    FileKit.compressImage(
                        bytes = bytes,
                        quality = 85,
                        imageFormat = ImageFormat.JPEG,
                    )

                val dest = (foodDirectory / "$uuid.jpg").apply { write(compressed) }
                dest.path
            } else {
                null
            }

        val entity =
            mapper.toEntity(
                uuid = uuid,
                name = name,
                brand = brand,
                barcode = barcode,
                note = note,
                imagePath = photoPath,
                source = source,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                accountId = accountId,
                isLiquid = isLiquid,
            )

        dao.insert(entity)

        return FoodProductIdentity.Local(uuid, LocalAccountId(entity.accountId))
    }

    override suspend fun edit(
        identity: FoodProductIdentity.Local,
        name: FoodName,
        brand: FoodBrand?,
        barcode: Barcode?,
        note: FoodNote?,
        imageUri: String?,
        source: FoodSource.UserAdded?,
        nutritionFacts: NutritionFacts,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        accountId: LocalAccountId,
        isLiquid: Boolean,
    ) {
        val existingEntity = dao.observe(identity.id, accountId.value).first()

        requireNotNull(existingEntity) {
            "Cannot edit non-existing food product with id: ${identity.id}"
        }

        val uuid = identity.id
        val foodDirectory = accountId.directory() / "food"
        foodDirectory.createDirectories()

        val imagePath: String? =
            if (existingEntity.photoPath != imageUri) {
                if (imageUri != null) {
                    val sourceFile = PlatformFile(imageUri)
                    require(sourceFile.exists()) { "Image file does not exist at path: $imageUri" }
                    val bytes = sourceFile.readBytes()

                    val compressed =
                        FileKit.compressImage(
                            bytes = bytes,
                            quality = 85,
                            imageFormat = ImageFormat.JPEG,
                        )

                    val dest = (foodDirectory / "$uuid.jpg").apply { write(compressed) }

                    dest.path
                } else {
                    existingEntity.photoPath?.let { existingPath ->
                        val existingFile = PlatformFile(existingPath)
                        if (existingFile.exists()) {
                            existingFile.delete()
                        }
                    }
                    null
                }
            } else {
                existingEntity.photoPath
            }

        val updatedEntity =
            mapper.toEntity(
                id = existingEntity.sqliteId,
                uuid = uuid,
                name = name,
                brand = brand,
                barcode = barcode,
                note = note,
                imagePath = imagePath,
                source = source,
                nutritionFacts = nutritionFacts,
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
                accountId = accountId,
                isLiquid = isLiquid,
            )

        dao.update(updatedEntity)
    }

    override fun observe(identity: FoodProductIdentity.Local): Flow<FoodProductDto?> =
        dao.observe(identity.id, identity.accountId.value).map { entity ->
            entity?.let(mapper::foodProductDto)
        }

    override suspend fun delete(identity: FoodProductIdentity.Local) {
        val existingEntity = dao.observe(identity.id, identity.accountId.value).first()

        requireNotNull(existingEntity) {
            "Cannot delete non-existing food product with id: ${identity.id}"
        }

        dao.delete(existingEntity)
        PlatformFile("${existingEntity.uuid}.jpg").delete(mustExist = false)
    }
}
