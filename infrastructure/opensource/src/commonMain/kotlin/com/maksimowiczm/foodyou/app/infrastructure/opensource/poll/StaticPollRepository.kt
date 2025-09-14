package com.maksimowiczm.foodyou.app.infrastructure.opensource.poll

import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.LinkPoll
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.Poll
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.PollId
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.PollRepository
import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@OptIn(ExperimentalTime::class)
internal class StaticPollRepository(private val dateProvider: DateProvider) : PollRepository {
    override fun observeActivePolls(): Flow<List<Poll>> {
        val activePolls =
            listOf<Poll>(FirstFoodYou3Poll).filter { it.expireDateTime > dateProvider.nowInstant() }

        return flowOf(activePolls)
    }
}

@OptIn(ExperimentalTime::class)
private val FirstFoodYou3Poll =
    LinkPoll(
        id = PollId("Food You 9.09.2025"),
        expireDateTime = LocalDateTime(2025, 9, 30, 23, 59).toInstant(TimeZone.UTC),
        title = "Your opinion matters!",
        description = "Help guide the app’s next steps by voting in Food You feature poll",
        url =
            "https://docs.google.com/forms/d/e/1FAIpQLSedg7Ofb2-r8mob_tUD1uxl9_PPMj7zfrHU6PB-w5_HNZAIHg/viewform?usp=header",
    )
