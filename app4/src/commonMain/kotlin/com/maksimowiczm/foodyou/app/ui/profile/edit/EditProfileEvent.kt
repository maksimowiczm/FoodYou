package com.maksimowiczm.foodyou.app.ui.profile.edit

sealed interface EditProfileEvent {
    data object Edited : EditProfileEvent

    data object Deleted : EditProfileEvent
}
