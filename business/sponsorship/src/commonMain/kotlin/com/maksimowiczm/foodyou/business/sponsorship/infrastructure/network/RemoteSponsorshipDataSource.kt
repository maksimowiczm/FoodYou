package com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal interface RemoteSponsorshipDataSource {
    suspend fun getSponsorships(
        before: Instant? = null,
        after: Instant? = null,
        size: Int = 20,
    ): PagedSponsorshipsResponse
}
