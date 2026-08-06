package com.maksimowiczm.foodyou.features.profile.edit

internal sealed interface EditProfileEvent {
    data object Edited : EditProfileEvent

    data object Deleted : EditProfileEvent
}
