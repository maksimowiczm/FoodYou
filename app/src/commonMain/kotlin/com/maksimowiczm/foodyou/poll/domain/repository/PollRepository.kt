package com.maksimowiczm.foodyou.poll.domain.repository

import com.maksimowiczm.foodyou.poll.domain.entity.Poll
import kotlinx.coroutines.flow.Flow

interface PollRepository {
    fun observeActivePolls(): Flow<List<Poll>>
}
