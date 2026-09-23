package com.maksimowiczm.foodyou.features.diary

import androidx.lifecycle.SavedStateHandle
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
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.toSnapshot
import com.maksimowiczm.foodyou.shared.ui.extension.now
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.toSnapshot
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.toSnapshot
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class AddFoodDiaryEntryViewModel(
    private val initialMealId: MealId,
    initialDate: LocalDate?,
    private val foodDiaryService: FoodDiaryService,
    mealPlanService: MealPlanService,
    appProfileManager: AppProfileManager,
    accountService: AccountService,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    @Serializable
    private data class Overrides(
        val mealId: Uuid? = null,
        val dateTime: LocalDateTime? = null,
        val profileIds: List<ProfileId>? = null,
    )

    private val overrides = MutableStateFlow(restoreOverrides())

    private fun restoreOverrides(): Overrides =
        savedStateHandle.get<String>(OVERRIDES_KEY)?.let {
            runCatching { Json.decodeFromString<Overrides>(it) }.getOrNull()
        } ?: Overrides()

    private fun updateOverrides(transform: (Overrides) -> Overrides) {
        overrides.update(transform)
        savedStateHandle[OVERRIDES_KEY] = Json.encodeToString(overrides.value)
    }

    private val appProfile = appProfileManager.observeAppProfileId()

    private val appProfileId =
        appProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = runBlocking { appProfile.first() },
        )

    private val initialDateTime =
        LocalDateTime.now().let { now ->
            if (initialDate != null) LocalDateTime(initialDate, now.time) else now
        }

    private val initialProfileIds = appProfileId.value?.let { listOf(it) } ?: emptyList()

    val uiState: StateFlow<AddFoodDiaryEntryUiState> =
        combine(
                accountService.observe().filterNotNull().map { account ->
                    account.profiles.map { ProfileUiState(it.id, it.name, it.avatar) }
                },
                mealPlanService.observe().filterNotNull().map { it.meals },
                overrides,
            ) { profiles, meals, overrides ->
                val selectedMeal =
                    meals.find { it.id.value == overrides.mealId }
                        ?: meals.find { it.id == initialMealId }
                        ?: meals.firstOrNull()
                        ?: return@combine AddFoodDiaryEntryUiState.Loading

                AddFoodDiaryEntryUiState.Ready(
                    profiles = profiles,
                    meals = meals,
                    selectedMeal = selectedMeal,
                    selectedDateTime = overrides.dateTime ?: initialDateTime,
                    selectedProfileIds = overrides.profileIds ?: initialProfileIds,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = AddFoodDiaryEntryUiState.Loading,
            )

    private val eventBus = Channel<FoodDiaryEntryCreatedUiEvent>()
    val createdUiEvent = eventBus.receiveAsFlow()

    fun selectMeal(mealId: MealId) = updateOverrides { it.copy(mealId = mealId.value) }

    fun selectProfiles(profileIds: List<ProfileId>) = updateOverrides {
        it.copy(profileIds = profileIds)
    }

    fun selectDate(date: LocalDate) = updateDateTime { LocalDateTime(date, it.time) }

    fun selectTime(time: LocalTime) = updateDateTime { LocalDateTime(it.date, time) }

    private fun updateDateTime(transform: (LocalDateTime) -> LocalDateTime) {
        val current = (uiState.value as? AddFoodDiaryEntryUiState.Ready)?.selectedDateTime ?: return
        updateOverrides { it.copy(dateTime = transform(current)) }
    }

    private fun create(snapshot: MeasuredFoodSnapshot) {
        val state = uiState.value as? AddFoodDiaryEntryUiState.Ready ?: return

        viewModelScope.launch {
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = state.selectedProfileIds.toSet(),
                        snapshot = snapshot,
                        mealId = state.selectedMeal.id,
                        entryTimestamp =
                            state.selectedDateTime.toInstant(TimeZone.currentSystemDefault()),
                    ),
            )
            eventBus.send(FoodDiaryEntryCreatedUiEvent(entryId))
        }
    }

    fun create(product: FoodDataCentralProduct, quantity: Quantity, isTracked: Boolean) {
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
        create(snapshot = if (isTracked) snapshot else snapshot.anonymize())
    }

    fun create(product: OpenFoodFactsProduct, quantity: Quantity, isTracked: Boolean) {
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
        create(snapshot = if (isTracked) snapshot else snapshot.anonymize())
    }

    fun create(product: UserProduct, quantity: Quantity, isTracked: Boolean) {
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
        create(snapshot = if (isTracked) snapshot else snapshot.anonymize())
    }

    fun create(recipe: UserRecipe, quantity: Quantity, isTracked: Boolean) {
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
        create(snapshot = if (isTracked) snapshot else snapshot.anonymize())
    }

    private companion object {
        const val OVERRIDES_KEY = "add_diary_overrides"
    }
}
