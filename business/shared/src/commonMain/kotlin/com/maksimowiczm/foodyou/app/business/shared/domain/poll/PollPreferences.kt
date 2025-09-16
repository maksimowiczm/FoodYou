package com.maksimowiczm.foodyou.app.business.shared.domain.poll

import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences

data class PollPreferences(val dismissedPolls: Set<PollId>) : UserPreferences
