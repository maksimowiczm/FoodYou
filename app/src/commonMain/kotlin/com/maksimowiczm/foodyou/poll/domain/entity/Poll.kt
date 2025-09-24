package com.maksimowiczm.foodyou.poll.domain.entity

import kotlin.jvm.JvmInline
import kotlin.time.Instant

@JvmInline value class PollId(val value: String)

sealed interface Poll {
    val id: PollId
    val expireDateTime: Instant
    val title: String
    val description: String
}

data class LinkPoll(
    override val id: PollId,
    override val expireDateTime: Instant,
    override val title: String,
    override val description: String,
    val url: String,
) : Poll
