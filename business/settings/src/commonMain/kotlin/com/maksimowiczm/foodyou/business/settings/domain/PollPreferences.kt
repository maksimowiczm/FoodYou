package com.maksimowiczm.foodyou.business.settings.domain

import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences

data class PollPreferences(val dismissedPolls: Set<PollId>) : UserPreferences
