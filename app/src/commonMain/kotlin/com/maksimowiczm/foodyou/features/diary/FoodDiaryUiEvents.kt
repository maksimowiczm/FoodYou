package com.maksimowiczm.foodyou.features.diary

import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity

sealed interface FoodDiaryUiEvents {
    data class Created(val entryId: FoodDiaryEntryIdentity) : FoodDiaryUiEvents
}
