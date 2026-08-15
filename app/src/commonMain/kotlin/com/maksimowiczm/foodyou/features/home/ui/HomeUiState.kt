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
