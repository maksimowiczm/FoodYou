package com.maksimowiczm.foodyou.business.settings.domain

import kotlin.jvm.JvmInline
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@JvmInline value class PollId(val value: String)

@OptIn(ExperimentalTime::class)
sealed interface Poll {
    val id: PollId
    val expireDateTime: Instant
    val title: String
    val description: String
}

@OptIn(ExperimentalTime::class)
data class LinkPoll(
    override val id: PollId,
    override val expireDateTime: Instant,
    override val title: String,
    override val description: String,
    val url: String,
) : Poll
