package com.maksimowiczm.foodyou.business.settings.domain

import kotlinx.coroutines.flow.Flow

interface PollRepository {
    fun observeActivePolls(): Flow<List<Poll>>
}
