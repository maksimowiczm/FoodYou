package com.maksimowiczm.foodyou.features.home.ui

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Immutable
data class HomeUiState(
    val profiles: List<Profile> = emptyList(),
    val selectedProfileId: ProfileId? = null,
    val date: LocalDate,
    val meals: List<HomeMealState> = emptyList(),
    val activeMealId: MealId? = null,
    val homeCardsOrder: List<HomeCard> = HomeCard.defaultOrder,
) {
    val selectedProfile = profiles.singleOrNull { it.id == selectedProfileId }
    val activeMeal =
        meals.filterIsInstance<HomeMealState.Linked>().singleOrNull { it.id == activeMealId }
}

sealed interface HomeMealState {
    val foods: List<HomeFoodState>

    @Immutable data class Unlinked(override val foods: List<HomeFoodState>) : HomeMealState

    @Immutable
    data class Linked(
        val id: MealId,
        val name: String,
        override val foods: List<HomeFoodState>,
    ) : HomeMealState
}

sealed interface HomeFoodState {
    val id: Id
    val time: LocalTime
    val measuredNutritionFacts: NutritionFacts

    @Serializable
    sealed interface Id {
        @Serializable data class FoodDiary(val value: FoodDiaryEntryId) : Id

        @Serializable data class QuickAdd(val value: QuickFoodDiaryEntryId) : Id
    }

    @Immutable
    data class FoodDiaryEntry(
        override val id: Id.FoodDiary,
        override val time: LocalTime,
        val snapshot: MeasuredFoodSnapshot,
    ) : HomeFoodState {
        override val measuredNutritionFacts = snapshot.measuredNutritionFacts
    }

    @Immutable
    data class QuickAdd(
        override val id: Id.QuickAdd,
        override val time: LocalTime,
        val name: String,
        override val measuredNutritionFacts: NutritionFacts,
    ) : HomeFoodState
}
