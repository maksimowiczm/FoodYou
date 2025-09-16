package com.maksimowiczm.foodyou.app.business.shared.domain.poll

import kotlinx.coroutines.flow.Flow

interface PollRepository {
    fun observeActivePolls(): Flow<List<Poll>>
}
