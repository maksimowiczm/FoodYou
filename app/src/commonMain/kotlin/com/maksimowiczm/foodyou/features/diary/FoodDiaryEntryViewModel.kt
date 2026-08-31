package com.maksimowiczm.foodyou.features.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.CompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class FoodDiaryEntryViewModel(
    private val mealIdentity: MealIdentity,
    private val foodDiaryService: FoodDiaryService,
    appProfileManager: AppProfileManager,
    accountService: AccountService,
) : ViewModel() {

    private val _appProfileId = appProfileManager.observeAppProfileId()

    val appProfileId =
        _appProfileId.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds),
            runBlocking { _appProfileId.first() },
        )

    val profiles =
        accountService
            .observe()
            .filterNotNull()
            .map { account ->
                account.profiles.map { profile ->
                    ProfileUiState(id = profile.id, name = profile.name, avatar = profile.avatar)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = null,
            )

    private val eventBus = Channel<FoodDiaryUiEvents>()
    val uiEvents = eventBus.receiveAsFlow()

    fun create(
        composition: MeasuredFoodSnapshot,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
    ) {
        viewModelScope.launch {
            val entryId =
                foodDiaryService.create(
                    profileIds = profiles.toSet(),
                    composition = composition,
                    mealIdentity = mealIdentity,
                    timestamp = timestamp.toInstant(TimeZone.currentSystemDefault()),
                )
            eventBus.send(FoodDiaryUiEvents.Created(entryId))
        }
    }

    fun create(
        product: FoodDataCentralProduct,
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
    ) {
        create(
            composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = FoodSnapshotId.FoodDataCentral(product.identity.fdcId),
                            name = FoodName(fallback = product.name),
                            brand = null,
                            image = null,
                            nutritionFacts = product.nutritionFacts,
                        ),
                    quantity =
                        FoodSnapshotQuantityUpdateService.map(
                            quantity = quantity,
                            servingWeight = product.servingQuantity?.forceWeight(),
                            packageWeight = product.packageQuantity?.forceWeight(),
                        ),
                ),
            profiles = profiles,
            timestamp = timestamp,
        )
    }

    fun create(
        product: OpenFoodFactsProduct,
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
    ) {
        create(
            composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = FoodSnapshotId.OpenFoodFacts(product.identity.barcode),
                            name = product.name,
                            brand = null,
                            image = product.image?.let { FoodSnapshotImage.Uri(it) },
                            nutritionFacts = product.nutritionFacts,
                        ),
                    quantity =
                        FoodSnapshotQuantityUpdateService.map(
                            quantity = quantity,
                            servingWeight = product.servingQuantity?.forceWeight(),
                            packageWeight = product.packageQuantity?.forceWeight(),
                        ),
                ),
            profiles = profiles,
            timestamp = timestamp,
        )
    }

    fun create(
        product: UserProduct,
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
    ) {
        create(
            composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(product.identity.id),
                            name = product.name,
                            brand = null,
                            image = product.image?.let { FoodSnapshotImage.Blob(it) },
                            nutritionFacts = product.nutritionFacts,
                        ),
                    quantity =
                        FoodSnapshotQuantityUpdateService.map(
                            quantity = quantity,
                            servingWeight = product.servingQuantity?.forceWeight(),
                            packageWeight = product.packageQuantity?.forceWeight(),
                        ),
                ),
            profiles = profiles,
            timestamp = timestamp,
        )
    }

    fun create(
        recipe: UserRecipe,
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
    ) {
        create(
            composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        CompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipe.identity.id),
                            name = recipe.name,
                            brand = null,
                            image = recipe.image?.let { FoodSnapshotImage.Blob(it) },
                            components = recipe.components,
                        ),
                    quantity =
                        FoodSnapshotQuantityUpdateService.map(
                            quantity = quantity,
                            servingWeight = recipe.servingWeight,
                            packageWeight = recipe.totalWeight,
                        ),
                ),
            profiles = profiles,
            timestamp = timestamp,
        )
    }
}
