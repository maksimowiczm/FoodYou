package com.maksimowiczm.foodyou.business.sponsorship.infrastructure.persistence.room

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.network.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.sponsorship.SponsorshipDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.sponsorship.SponsorshipEntity
import com.maksimowiczm.foodyou.business.sponsorship.domain.Sponsorship
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.persistence.LocalSponsorshipDataSource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal class RoomSponsorshipDataSource(private val sponsorshipDao: SponsorshipDao) :
    LocalSponsorshipDataSource {
    override suspend fun getLatestSponsorship(): Sponsorship? =
        sponsorshipDao.getLatestSponsorship()?.toModel()

    override suspend fun getOldestSponsorship(): Sponsorship? =
        sponsorshipDao.getOldestSponsorship()?.toModel()

    override suspend fun upsertSponsorships(sponsorships: List<Sponsorship>) {
        sponsorshipDao.upsert(sponsorships.map { it.toEntity() })
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun observeSponsorships(
        config: PagingConfig,
        remoteMediatorFactory: RemoteMediatorFactory?,
    ): Flow<PagingData<Sponsorship>> =
        Pager(
                config = config,
                pagingSourceFactory = { sponsorshipDao.pagedFromLatest() },
                remoteMediator = remoteMediatorFactory?.create(),
            )
            .flow
            .map { data -> data.map { it.toModel() } }
}

@OptIn(ExperimentalTime::class)
private fun Sponsorship.toEntity(): SponsorshipEntity =
    SponsorshipEntity(
        id = id,
        sponsorName = sponsorName,
        message = message,
        amount = amount,
        currency = currency,
        inEuro = inEuro,
        sponsorshipEpochSeconds = dateTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
        method = method,
    )

@OptIn(ExperimentalTime::class)
private fun SponsorshipEntity.toModel(): Sponsorship =
    Sponsorship(
        id = id,
        sponsorName = sponsorName,
        message = message,
        amount = amount,
        currency = currency,
        inEuro = inEuro,
        dateTime =
            Instant.fromEpochSeconds(sponsorshipEpochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        method = method,
    )
