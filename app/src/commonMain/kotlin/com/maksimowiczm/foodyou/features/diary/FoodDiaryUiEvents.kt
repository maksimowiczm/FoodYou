package com.maksimowiczm.foodyou.features.diary

import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId

sealed interface FoodDiaryUiEvents {
    data class Created(val entryId: FoodDiaryEntryId) : FoodDiaryUiEvents
}
