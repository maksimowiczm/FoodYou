package com.maksimowiczm.foodyou.features.diary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.fooddiary.application.QuickFoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.preferences.domain.EnergyUnitPreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
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

sealed interface UpdateQuickFoodDiaryEntryUiState {
    data object Loading : UpdateQuickFoodDiaryEntryUiState

    data class Ready(
        val entry: QuickFoodDiaryEntry,
        val profiles: List<ProfileUiState>,
        val meals: List<Meal>,
        val selectedMeal: Meal,
        val selectedDateTime: LocalDateTime,
        val selectedProfileIds: List<ProfileId>,
        val energyUnit: EnergyUnit,
    ) : UpdateQuickFoodDiaryEntryUiState
}

class UpdateQuickFoodDiaryEntryViewModel(
    private val entryId: QuickFoodDiaryEntryId,
    private val quickFoodDiaryService: QuickFoodDiaryService,
    mealPlanService: MealPlanService,
    accountService: AccountService,
    userPreferencesRepository: UserPreferencesRepository,
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

    private val energyUnit =
        userPreferencesRepository
            .observe<EnergyUnitPreference>()
            .map { it.energyUnit }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = EnergyUnit.Kilocalories,
            )

    val uiState: StateFlow<UpdateQuickFoodDiaryEntryUiState> =
        combine(
                quickFoodDiaryService.observe(entryId).filterNotNull(),
                accountService.observe().filterNotNull().map { account ->
                    account.profiles.map { ProfileUiState(it.id, it.name, it.avatar) }
                },
                mealPlanService.observe().filterNotNull().map { it.meals },
                overrides,
                energyUnit,
            ) { entry, profiles, meals, overrides, energyUnit ->
                val selectedMeal =
                    meals.find { it.id.value == overrides.mealId }
                        ?: meals.find { it.id == entry.mealId }
                        ?: meals.firstOrNull()
                        ?: return@combine UpdateQuickFoodDiaryEntryUiState.Loading

                UpdateQuickFoodDiaryEntryUiState.Ready(
                    entry = entry,
                    profiles = profiles,
                    meals = meals,
                    selectedMeal = selectedMeal,
                    selectedDateTime =
                        overrides.dateTime
                            ?: entry.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()),
                    selectedProfileIds = overrides.profileIds ?: entry.profileIds.toList(),
                    energyUnit = energyUnit,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = UpdateQuickFoodDiaryEntryUiState.Loading,
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
            (uiState.value as? UpdateQuickFoodDiaryEntryUiState.Ready)?.selectedDateTime ?: return
        updateOverrides { it.copy(dateTime = transform(current)) }
    }

    fun update(
        name: String,
        proteins: Double?,
        carbohydrates: Double?,
        fats: Double?,
        energyValue: Double?,
    ) {
        val state = uiState.value as? UpdateQuickFoodDiaryEntryUiState.Ready ?: return
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return

        val nutritionFacts =
            NutritionFacts(
                proteins = proteins?.grams?.toNutrientValue() ?: NutrientValue.Incomplete(null),
                carbohydrates =
                    carbohydrates?.grams?.toNutrientValue() ?: NutrientValue.Incomplete(null),
                fats = fats?.grams?.toNutrientValue() ?: NutrientValue.Incomplete(null),
                energy =
                    energyValue?.let { Energy.from(it, state.energyUnit).toNutrientValue() }
                        ?: NutrientValue.Incomplete(null),
            )

        viewModelScope.launch {
            quickFoodDiaryService.handle(
                id = entryId,
                command =
                    QuickFoodDiaryEntryCommand.Update { current ->
                        current.copy(
                            profileIds = state.selectedProfileIds.toSet(),
                            name = trimmedName,
                            nutritionFacts = nutritionFacts,
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
        const val OVERRIDES_KEY = "update_quick_diary_overrides"
    }
}
