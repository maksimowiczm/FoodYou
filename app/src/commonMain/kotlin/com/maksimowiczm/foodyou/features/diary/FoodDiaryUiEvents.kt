package com.maksimowiczm.foodyou.features.diary

sealed interface FoodDiaryUiEvents {
    data object Created : FoodDiaryUiEvents
}
