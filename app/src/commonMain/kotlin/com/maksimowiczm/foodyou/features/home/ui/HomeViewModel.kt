package com.maksimowiczm.foodyou.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.extension.observe
import com.maksimowiczm.foodyou.features.home.integration.HomeDao
import com.maksimowiczm.foodyou.features.home.integration.HomeEntryEntity
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.mealplan.domain.activeMeal
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val mealPlanService: MealPlanService,
    private val appProfileManager: AppProfileManager,
    private val homeDao: HomeDao,
    accountService: AccountService,
) : ViewModel() {
    private val profiles =
        accountService.observe().filterNotNull().map { account -> account.profiles }

    fun selectProfile(profileId: ProfileId) {
        viewModelScope.launch { appProfileManager.setAppProfileId(profileId) }
    }

    private val userDate = MutableStateFlow<LocalDate?>(null)
    private val userMealIdentity = MutableStateFlow<MealIdentity?>(null)

    private val dateTime =
        combine(
            Clock.System.observe(1.minutes),
            userDate,
        ) { realTime, userDate ->
            val realDateTime = realTime.toLocalDateTime(TimeZone.currentSystemDefault())
            if (userDate != null)
                LocalDateTime(
                    year = userDate.year,
                    month = userDate.month,
                    day = userDate.day,
                    hour = realDateTime.hour,
                    minute = realDateTime.minute,
                    second = realDateTime.second,
                    nanosecond = realDateTime.nanosecond,
                )
            else realDateTime
        }

    private val activeMeal = userMealIdentity.flatMapLatest { mealIdentity ->
        if (mealIdentity == null)
            combine(
                mealPlanService.observe(),
                dateTime,
            ) { mealPlan, date ->
                mealPlan.activeMeal(date.time, 10.minutes)
            }
        else
            mealPlanService.observe().map { mealPlan ->
                mealPlan.meals.firstOrNull { it.identity == mealIdentity }
            }
    }

    fun selectDate(date: LocalDate) {
        userDate.value = date
    }

    fun selectMeal(mealIdentity: MealIdentity?) {
        userMealIdentity.value = mealIdentity
    }

    val uiState =
        combine(
                profiles,
                appProfileManager.observeAppProfileId().filterNotNull(),
                dateTime,
                activeMeal,
                mealPlanService.observe(),
            ) { profiles, selectedProfileId, dateTime, activeMeal, mealPlan ->
                val entries = homeDao.observeEntries(selectedProfileId.value, dateTime.date)
                entries.map { homeEntries ->
                    val mealIds = mealPlan.meals.map { it.identity.id }.toSet()

                    val linkedMeals =
                        mealPlan.meals.map { meal ->
                            HomeMealState.Linked(
                                identity = meal.identity,
                                name = meal.name,
                                foods =
                                    homeEntries
                                        .filter { it.mealId == meal.identity.id }
                                        .toFoodStates(),
                            )
                        }

                    val unlinkedFoods =
                        homeEntries
                            .filter { it.mealId == null || it.mealId !in mealIds }
                            .toFoodStates()

                    val meals =
                        if (unlinkedFoods.isNotEmpty())
                            linkedMeals + HomeMealState.Unlinked(foods = unlinkedFoods)
                        else linkedMeals

                    HomeUiState(
                        profiles = profiles,
                        selectedProfileId = selectedProfileId,
                        date = dateTime.date,
                        meals = meals,
                        activeMealId = activeMeal?.identity,
                    )
                }
            }
            .flatMapLatest { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue =
                    HomeUiState(
                        date =
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    ),
            )

    private fun List<HomeEntryEntity>.toFoodStates() = map { entry ->
        HomeFoodState(
            identity = FoodDiaryEntryIdentity(entry.entryId),
            time = entry.time,
            snapshot = entry.composition,
        )
    }
}
