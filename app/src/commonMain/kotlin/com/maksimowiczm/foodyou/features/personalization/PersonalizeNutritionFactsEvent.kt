package com.maksimowiczm.foodyou.features.personalization

sealed interface PersonalizeNutritionFactsEvent {
    data object Updated : PersonalizeNutritionFactsEvent
}
