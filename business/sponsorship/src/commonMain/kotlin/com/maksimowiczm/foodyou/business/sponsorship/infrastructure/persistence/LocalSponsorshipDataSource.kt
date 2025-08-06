package com.maksimowiczm.foodyou.business.sponsorship.infrastructure.persistence

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.business.shared.infrastructure.network.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.sponsorship.domain.Sponsorship
import kotlinx.coroutines.flow.Flow

internal interface LocalSponsorshipDataSource {
    suspend fun getLatestSponsorship(): Sponsorship?

    suspend fun getOldestSponsorship(): Sponsorship?

    suspend fun upsertSponsorships(sponsorships: List<Sponsorship>)

    fun observeSponsorships(
        config: PagingConfig,
        remoteMediatorFactory: RemoteMediatorFactory?,
    ): Flow<PagingData<Sponsorship>>
}
