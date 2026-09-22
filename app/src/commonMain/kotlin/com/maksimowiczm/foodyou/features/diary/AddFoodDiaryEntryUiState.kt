package com.maksimowiczm.foodyou.features.diary

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import kotlinx.datetime.LocalDateTime

sealed interface AddFoodDiaryEntryUiState {
    data object Loading : AddFoodDiaryEntryUiState

    @Immutable
    data class Ready(
        val profiles: List<ProfileUiState>,
        val meals: List<Meal>,
        val selectedMeal: Meal,
        val selectedDateTime: LocalDateTime,
        val selectedProfileIds: List<ProfileId>,
    ) : AddFoodDiaryEntryUiState
}
