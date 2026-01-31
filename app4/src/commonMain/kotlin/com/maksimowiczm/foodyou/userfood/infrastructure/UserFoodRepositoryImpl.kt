package com.maksimowiczm.foodyou.userfood.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Barcode
import com.maksimowiczm.foodyou.common.domain.food.FoodBrand
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.food.FoodNote
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.infrastructure.filekit.directory
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProduct
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import com.maksimowiczm.foodyou.userfood.domain.UserFoodSearchParameters
import com.maksimowiczm.foodyou.userfood.infrastructure.room.UserFoodDao
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

internal class UserFoodRepositoryImpl(
    private val dao: UserFoodDao,
    private val nameSelector: FoodNameSelector,
) : UserFoodRepository {
    private val mapper = UserFoodMapper()

    @OptIn(ExperimentalPagingApi::class)
    override fun search(
        parameters: UserFoodSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<UserFoodProduct>> {
        val language = nameSelector.select()

        when (parameters.query) {
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
            when (parameters.query) {
                SearchQuery.Blank -> dao.getPagingSource(language.tag, parameters.accountId.value)

                is SearchQuery.Barcode ->
                    dao.getPagingSourceByBarcode(
                        parameters.query.barcode,
                        language.tag,
                        parameters.accountId.value,
                    )

                is SearchQuery.Text ->
                    dao.getPagingSourceByQuery(
                        parameters.query.query,
                        language.tag,
                        parameters.accountId.value,
                    )

                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> error("Unreachable")
            }
        }

        return Pager(config = config, pagingSourceFactory = factory).flow.map { data ->
            data.map(mapper::userFoodProduct)
        }
    }

    override fun count(parameters: UserFoodSearchParameters): Flow<Int> {
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
    ): UserFoodProductIdentity {
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

        return UserFoodProductIdentity(uuid, LocalAccountId(entity.accountId))
    }

    override suspend fun edit(
        identity: UserFoodProductIdentity,
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

    override fun observe(identity: UserFoodProductIdentity): Flow<UserFoodProduct?> =
        dao.observe(identity.id, identity.accountId.value).map { entity ->
            entity?.let(mapper::userFoodProduct)
        }

    override suspend fun delete(identity: UserFoodProductIdentity) {
        val existingEntity = dao.observe(identity.id, identity.accountId.value).first()

        requireNotNull(existingEntity) {
            "Cannot delete non-existing food product with id: ${identity.id}"
        }

        dao.delete(existingEntity)
        PlatformFile("${existingEntity.uuid}.jpg").delete(mustExist = false)
    }
}
