package com.maksimowiczm.foodyou.poll.domain.entity

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferences

data class PollPreferences(val dismissedPolls: Set<PollId>) : UserPreferences
