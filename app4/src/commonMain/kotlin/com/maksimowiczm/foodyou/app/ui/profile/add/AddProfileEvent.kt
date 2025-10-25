package com.maksimowiczm.foodyou.app.ui.profile.add

import com.maksimowiczm.foodyou.common.domain.ProfileId

sealed interface AddProfileEvent {
    data class Created(val profileId: ProfileId) : AddProfileEvent
}
