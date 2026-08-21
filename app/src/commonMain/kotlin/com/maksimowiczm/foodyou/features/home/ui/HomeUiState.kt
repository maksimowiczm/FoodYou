package com.maksimowiczm.foodyou.features.home.ui

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class HomeUiState(
    val profiles: List<Profile> = emptyList(),
    val selectedProfileId: ProfileId? = null,
    val date: LocalDate,
    val meals: List<HomeMealState> = emptyList(),
    val activeMealId: MealIdentity? = null,
) {
    val selectedProfile = profiles.singleOrNull { it.id == selectedProfileId }
    val activeMeal =
        meals.filterIsInstance<HomeMealState.Linked>().singleOrNull { it.identity == activeMealId }
}

sealed interface HomeMealState {
    val foods: List<HomeFoodState>

    @Immutable data class Unlinked(override val foods: List<HomeFoodState>) : HomeMealState

    @Immutable
    data class Linked(
        val identity: MealIdentity,
        val name: String,
        override val foods: List<HomeFoodState>,
    ) : HomeMealState
}

@Immutable
data class HomeFoodState(
    val identity: FoodDiaryEntryIdentity,
    val time: LocalTime,
    val component: FoodCompositionComponent,
)
