package com.maksimowiczm.foodyou.features.home

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.common.extension.observe
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.mealplan.domain.activeMealStrict
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
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
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val mealPlanService: MealPlanService,
    private val appProfileManager: AppProfileManager,
    accountService: AccountService,
) : ViewModel() {
    private val profiles =
        accountService.observe().filterNotNull().map { account ->
            account.profiles.map { profile ->
                ProfileUiState(id = profile.id, name = profile.name, avatar = profile.avatar)
            }
        }

    fun selectProfile(profile: ProfileUiState) {
        viewModelScope.launch { appProfileManager.setAppProfileId(profile.id) }
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
                mealPlan.activeMealStrict(date.time)
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
                appProfileManager.observeAppProfileId(),
                dateTime,
                activeMeal,
                mealPlanService.observe(),
            ) { profiles, selectedProfileId, dateTime, activeMeal, mealPlan ->
                val meals =
                    mealPlan.meals.map { meal ->
                        val random =
                            Random(
                                meal.identity.id.hashCode() +
                                    dateTime.date.toEpochDays() +
                                    (selectedProfileId?.value?.hashCode() ?: 0)
                            )
                        HomeMealState(
                            identity = meal.identity,
                            name = meal.name,
                            foods =
                                List(random.nextInt(4)) {
                                    val template = fakeFoodTemplates.random(random)
                                    anonymousFood(
                                        identity =
                                            FoodDiaryEntryIdentity(
                                                Uuid.fromLongs(
                                                    random.nextLong(),
                                                    random.nextLong(),
                                                )
                                            ),
                                        hour = random.nextInt(24),
                                        minute = random.nextInt(60),
                                        name = template.name,
                                        nutritionFacts = template.nutritionFacts,
                                        quantity = template.quantity,
                                    )
                                },
                        )
                    }

                HomeUiState(
                    profiles = profiles,
                    selectedProfile = profiles.find { it.id == selectedProfileId },
                    date = dateTime.date,
                    meals = meals,
                    activeMeal =
                        activeMeal?.let { am -> meals.find { it.identity == am.identity } },
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue =
                    HomeUiState(
                        date =
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    ),
            )
}

val fakeHomeState =
    HomeUiState(
        date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        meals =
            listOf(
                HomeMealState(
                    identity = MealIdentity(Uuid.random()),
                    name = "Breakfast",
                    foods =
                        listOf(
                            anonymousFood(
                                identity = FoodDiaryEntryIdentity(Uuid.random()),
                                hour = 7,
                                minute = 45,
                                name = "Oatmeal with Blueberries",
                                nutritionFacts =
                                    NutritionFacts(
                                        proteins = 12.grams.toNutrientValue(),
                                        carbohydrates = 48.grams.toNutrientValue(),
                                        fats = 7.grams.toNutrientValue(),
                                        energy = 290.kilocalories.toNutrientValue(),
                                    ),
                                quantity =
                                    FoodComponentComponentQuantity.Serving(
                                        servings = 1.0,
                                        servingWeight = 250.grams,
                                        packageWeight = null,
                                    ),
                            )
                        ),
                ),
                HomeMealState(
                    identity = MealIdentity(Uuid.random()),
                    name = "Lunch",
                    foods =
                        listOf(
                            anonymousFood(
                                identity = FoodDiaryEntryIdentity(Uuid.random()),
                                hour = 12,
                                minute = 30,
                                name = "Grilled Chicken Breast",
                                nutritionFacts =
                                    NutritionFacts(
                                        proteins = 42.grams.toNutrientValue(),
                                        carbohydrates = 0.grams.toNutrientValue(),
                                        fats = 5.grams.toNutrientValue(),
                                        energy = 220.kilocalories.toNutrientValue(),
                                    ),
                                quantity =
                                    FoodComponentComponentQuantity.Weight(
                                        servingWeight = null,
                                        packageWeight = null,
                                        absoluteWeight = 180.grams,
                                    ),
                            )
                        ),
                ),
            ),
    )

@Immutable
data class HomeUiState(
    val profiles: List<ProfileUiState> = emptyList(),
    val selectedProfile: ProfileUiState? = null,
    val date: LocalDate,
    val meals: List<HomeMealState> = emptyList(),
    val activeMeal: HomeMealState? = null,
)

@Immutable
data class ProfileUiState(val id: ProfileId, val name: String, val avatar: Profile.Avatar)

@Immutable
data class HomeMealState(
    val identity: MealIdentity,
    val name: String,
    val foods: List<HomeFoodState>,
)

@Immutable
data class HomeFoodState(
    val identity: FoodDiaryEntryIdentity,
    val time: LocalTime,
    val component: FoodCompositionComponent,
)

private data class FoodTemplate(
    val name: String,
    val nutritionFacts: NutritionFacts,
    val quantity: FoodComponentComponentQuantity,
)

private val fakeFoodTemplates =
    listOf(
        FoodTemplate(
            name = "Oatmeal with Blueberries",
            nutritionFacts =
                NutritionFacts(
                    proteins = 12.grams.toNutrientValue(),
                    carbohydrates = 48.grams.toNutrientValue(),
                    fats = 7.grams.toNutrientValue(),
                    energy = 290.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Serving(
                    servings = 1.0,
                    servingWeight = 250.grams,
                    packageWeight = null,
                ),
        ),
        FoodTemplate(
            name = "Cappuccino",
            nutritionFacts =
                NutritionFacts(
                    proteins = 6.grams.toNutrientValue(),
                    carbohydrates = 9.grams.toNutrientValue(),
                    fats = 4.grams.toNutrientValue(),
                    energy = 95.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Serving(
                    servings = 1.0,
                    servingWeight = 250.grams,
                    packageWeight = null,
                ),
        ),
        FoodTemplate(
            name = "Grilled Chicken Breast",
            nutritionFacts =
                NutritionFacts(
                    proteins = 42.grams.toNutrientValue(),
                    carbohydrates = 0.grams.toNutrientValue(),
                    fats = 5.grams.toNutrientValue(),
                    energy = 220.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Weight(
                    servingWeight = null,
                    packageWeight = null,
                    absoluteWeight = 180.grams,
                ),
        ),
        FoodTemplate(
            name = "Steamed Rice",
            nutritionFacts =
                NutritionFacts(
                    proteins = 5.grams.toNutrientValue(),
                    carbohydrates = 52.grams.toNutrientValue(),
                    fats = 1.grams.toNutrientValue(),
                    energy = 240.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Weight(
                    servingWeight = null,
                    packageWeight = null,
                    absoluteWeight = 200.grams,
                ),
        ),
        FoodTemplate(
            name = "Mixed Salad",
            nutritionFacts =
                NutritionFacts(
                    proteins = 2.grams.toNutrientValue(),
                    carbohydrates = 8.grams.toNutrientValue(),
                    fats = 6.grams.toNutrientValue(),
                    energy = 95.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Serving(
                    servings = 1.0,
                    servingWeight = 120.grams,
                    packageWeight = null,
                ),
        ),
        FoodTemplate(
            name = "Salmon Fillet",
            nutritionFacts =
                NutritionFacts(
                    proteins = 34.grams.toNutrientValue(),
                    carbohydrates = 0.grams.toNutrientValue(),
                    fats = 18.grams.toNutrientValue(),
                    energy = 310.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Weight(
                    servingWeight = null,
                    packageWeight = null,
                    absoluteWeight = 170.grams,
                ),
        ),
        FoodTemplate(
            name = "Roasted Potatoes",
            nutritionFacts =
                NutritionFacts(
                    proteins = 4.grams.toNutrientValue(),
                    carbohydrates = 38.grams.toNutrientValue(),
                    fats = 5.grams.toNutrientValue(),
                    energy = 220.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Weight(
                    servingWeight = null,
                    packageWeight = null,
                    absoluteWeight = 180.grams,
                ),
        ),
        FoodTemplate(
            name = "Protein Bar",
            nutritionFacts =
                NutritionFacts(
                    proteins = 20.grams.toNutrientValue(),
                    carbohydrates = 23.grams.toNutrientValue(),
                    fats = 7.grams.toNutrientValue(),
                    energy = 230.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Package(
                    packages = 1.0,
                    servingWeight = null,
                    packageWeight = 60.grams,
                ),
        ),
        FoodTemplate(
            name = "Apple",
            nutritionFacts =
                NutritionFacts(
                    proteins = 0.5.grams.toNutrientValue(),
                    carbohydrates = 25.grams.toNutrientValue(),
                    fats = 0.3.grams.toNutrientValue(),
                    energy = 95.kilocalories.toNutrientValue(),
                ),
            quantity =
                FoodComponentComponentQuantity.Serving(
                    servings = 1.0,
                    servingWeight = 180.grams,
                    packageWeight = null,
                ),
        ),
    )

private fun anonymousFood(
    identity: FoodDiaryEntryIdentity,
    hour: Int,
    minute: Int,
    name: String,
    nutritionFacts: NutritionFacts,
    quantity: FoodComponentComponentQuantity,
): HomeFoodState =
    HomeFoodState(
        identity = identity,
        time = LocalTime(hour = hour, minute = minute),
        component =
            FoodCompositionComponent.Anonymous(
                identity = FoodCompositionComponentIdentity.Anonymous(Uuid.random()),
                name = FoodName(fallback = name),
                image = null,
                nutritionFacts = nutritionFacts / quantity.absoluteWeight.grams * 100.0,
                quantity = quantity,
            ),
    )
