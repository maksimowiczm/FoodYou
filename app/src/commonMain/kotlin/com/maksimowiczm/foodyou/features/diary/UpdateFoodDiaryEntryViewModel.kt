package com.maksimowiczm.foodyou.features.diary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class UpdateFoodDiaryEntryViewModel(
    private val entryId: FoodDiaryEntryId,
    private val foodDiaryService: FoodDiaryService,
    mealPlanService: MealPlanService,
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

    val uiState: StateFlow<UpdateFoodDiaryEntryUiState> =
        combine(
                foodDiaryService.observe(entryId).filterNotNull(),
                accountService.observe().filterNotNull().map { account ->
                    account.profiles.map { ProfileUiState(it.id, it.name, it.avatar) }
                },
                mealPlanService.observe().filterNotNull().map { it.meals },
                overrides,
            ) { entry, profiles, meals, overrides ->
                val selectedMeal =
                    meals.find { it.id.value == overrides.mealId }
                        ?: meals.find { it.id == entry.mealId }
                        ?: meals.firstOrNull()
                        ?: return@combine UpdateFoodDiaryEntryUiState.Loading

                UpdateFoodDiaryEntryUiState.Ready(
                    entry = entry,
                    profiles = profiles,
                    meals = meals,
                    selectedMeal = selectedMeal,
                    selectedDateTime =
                        overrides.dateTime
                            ?: entry.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()),
                    selectedProfileIds = overrides.profileIds ?: entry.profileIds.toList(),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = UpdateFoodDiaryEntryUiState.Loading,
            )

    private val eventBus = Channel<FoodDiaryEntryUpdatedUiEvent>()
    val updatedEvent = eventBus.receiveAsFlow()

    fun selectMeal(mealId: MealId) = updateOverrides { it.copy(mealId = mealId.value) }

    fun selectProfiles(profileIds: List<ProfileId>) = updateOverrides {
        it.copy(profileIds = profileIds)
    }

    fun selectDate(date: LocalDate) = updateDateTime { LocalDateTime(date, it.time) }

    fun selectTime(time: LocalTime) = updateDateTime { LocalDateTime(it.date, time) }

    private fun updateDateTime(transform: (LocalDateTime) -> LocalDateTime) {
        val current =
            (uiState.value as? UpdateFoodDiaryEntryUiState.Ready)?.selectedDateTime ?: return
        updateOverrides { it.copy(dateTime = transform(current)) }
    }

    fun relink(relinkedSnapshot: MeasuredFoodSnapshot) = save { relinkedSnapshot }

    fun update(quantity: Quantity, isTracked: Boolean) = save { current ->
        val snapshot =
            current.snapshot.copy(
                quantity =
                    FoodSnapshotQuantityUpdateService.map(
                        quantity = quantity,
                        servingWeight = current.snapshot.quantity.servingWeight,
                        packageWeight = current.snapshot.quantity.packageWeight,
                    )
            )
        if (isTracked) snapshot else snapshot.anonymize()
    }

    /** Applies the selected profiles/meal/time plus the snapshot produced by [newSnapshot]. */
    private fun save(newSnapshot: (current: FoodDiaryEntry) -> MeasuredFoodSnapshot) {
        val state = uiState.value as? UpdateFoodDiaryEntryUiState.Ready ?: return

        viewModelScope.launch {
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Update { current ->
                        current.copy(
                            profileIds = state.selectedProfileIds.toSet(),
                            snapshot = newSnapshot(current),
                            timestamp =
                                state.selectedDateTime.toInstant(TimeZone.currentSystemDefault()),
                            mealId = state.selectedMeal.id,
                        )
                    },
            )
            eventBus.send(FoodDiaryEntryUpdatedUiEvent)
        }
    }

    private companion object {
        const val OVERRIDES_KEY = "update_diary_overrides"
    }
}
