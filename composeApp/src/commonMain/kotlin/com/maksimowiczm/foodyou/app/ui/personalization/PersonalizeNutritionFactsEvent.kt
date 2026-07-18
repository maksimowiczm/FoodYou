package com.maksimowiczm.foodyou.app.ui.personalization

sealed interface PersonalizeNutritionFactsEvent {
    data object Updated : PersonalizeNutritionFactsEvent
}
