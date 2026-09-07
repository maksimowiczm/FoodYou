package com.maksimowiczm.foodyou.features.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.toSnapshot
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.toSnapshot
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.toSnapshot
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.toSnapshot
import kotlin.time.Clock
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

class AddFoodDiaryEntryViewModel(
    private val mealId: MealId,
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

    private val eventBus = Channel<FoodDiaryEntryCreatedUiEvent>()
    val createdUiEvent = eventBus.receiveAsFlow()

    fun create(
        snapshot: MeasuredFoodSnapshot,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
    ) {
        viewModelScope.launch {
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = profiles.toSet(),
                        snapshot = snapshot,
                        mealId = mealId,
                        entryTimestamp = timestamp.toInstant(TimeZone.currentSystemDefault()),
                        timestamp = Clock.System.now(),
                    ),
            )
            eventBus.send(FoodDiaryEntryCreatedUiEvent(entryId))
        }
    }

    fun create(
        product: FoodDataCentralProduct,
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
        isTracked: Boolean,
    ) {
        val snapshot =
            MeasuredFoodSnapshot(
                snapshot = product.toSnapshot(),
                quantity =
                    FoodSnapshotQuantityUpdateService.map(
                        quantity = quantity,
                        servingWeight = product.servingQuantity?.forceWeight(),
                        packageWeight = product.packageQuantity?.forceWeight(),
                    ),
            )
        create(
            snapshot = if (isTracked) snapshot else snapshot.anonymize(),
            profiles = profiles,
            timestamp = timestamp,
        )
    }

    fun create(
        product: OpenFoodFactsProduct,
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
        isTracked: Boolean,
    ) {
        val snapshot =
            MeasuredFoodSnapshot(
                snapshot = product.toSnapshot(),
                quantity =
                    FoodSnapshotQuantityUpdateService.map(
                        quantity = quantity,
                        servingWeight = product.servingQuantity?.forceWeight(),
                        packageWeight = product.packageQuantity?.forceWeight(),
                    ),
            )
        create(
            snapshot = if (isTracked) snapshot else snapshot.anonymize(),
            profiles = profiles,
            timestamp = timestamp,
        )
    }

    fun create(
        product: UserProduct,
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
        isTracked: Boolean,
    ) {
        val snapshot =
            MeasuredFoodSnapshot(
                snapshot = product.toSnapshot(),
                quantity =
                    FoodSnapshotQuantityUpdateService.map(
                        quantity = quantity,
                        servingWeight = product.servingQuantity?.forceWeight(),
                        packageWeight = product.packageQuantity?.forceWeight(),
                    ),
            )
        create(
            snapshot = if (isTracked) snapshot else snapshot.anonymize(),
            profiles = profiles,
            timestamp = timestamp,
        )
    }

    fun create(
        recipe: UserRecipe,
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: LocalDateTime,
        isTracked: Boolean,
    ) {
        val snapshot =
            MeasuredFoodSnapshot(
                snapshot = recipe.toSnapshot(),
                quantity =
                    FoodSnapshotQuantityUpdateService.map(
                        quantity = quantity,
                        servingWeight = recipe.servingWeight,
                        packageWeight = recipe.totalWeight,
                    ),
            )
        create(
            snapshot = if (isTracked) snapshot else snapshot.anonymize(),
            profiles = profiles,
            timestamp = timestamp,
        )
    }
}
