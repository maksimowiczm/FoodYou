package com.maksimowiczm.foodyou.features.diary

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntry
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import kotlinx.datetime.LocalDateTime

sealed interface UpdateFoodDiaryEntryUiState {
    data object Loading : UpdateFoodDiaryEntryUiState

    @Immutable
    data class Ready(
        val entry: FoodDiaryEntry,
        val profiles: List<ProfileUiState>,
        val meals: List<Meal>,
        val selectedMeal: Meal,
        val selectedDateTime: LocalDateTime,
        val selectedProfileIds: List<ProfileId>,
    ) : UpdateFoodDiaryEntryUiState
}
