package com.maksimowiczm.foodyou.features.userrecipe.edit

sealed interface EditRecipeEvent {
    data object Updated : EditRecipeEvent
}
