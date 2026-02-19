package com.maksimowiczm.foodyou.app.ui.profile.add

import com.maksimowiczm.foodyou.common.domain.ProfileId

internal sealed interface AddProfileEvent {
    data class Created(val profileId: ProfileId) : AddProfileEvent
}
