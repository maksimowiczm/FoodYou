package com.maksimowiczm.foodyou.features.home.ui

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class HomeUiState(
    val profiles: List<Profile> = emptyList(),
    val selectedProfileId: ProfileId? = null,
    val date: LocalDate,
    val meals: List<HomeMealState> = emptyList(),
    val activeMealId: MealId? = null,
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

@Immutable
data class HomeFoodState(
    val id: FoodDiaryEntryId,
    val time: LocalTime,
    val snapshot: MeasuredFoodSnapshot,
)
