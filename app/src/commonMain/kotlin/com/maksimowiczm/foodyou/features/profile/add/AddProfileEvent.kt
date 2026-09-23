package com.maksimowiczm.foodyou.features.profile.add

import com.maksimowiczm.foodyou.common.domain.ProfileId

internal sealed interface AddProfileEvent {
    data class Created(val profileId: ProfileId) : AddProfileEvent
}
