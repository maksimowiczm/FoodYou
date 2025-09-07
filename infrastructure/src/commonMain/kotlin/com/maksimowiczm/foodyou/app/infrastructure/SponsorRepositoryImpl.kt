package com.maksimowiczm.foodyou.app.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.app.infrastructure.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.app.infrastructure.paging.SponsorshipRemoteMediator
import com.maksimowiczm.foodyou.app.infrastructure.room.sponsorship.SponsorshipDao
import com.maksimowiczm.foodyou.app.infrastructure.room.sponsorship.SponsorshipEntity
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Sponsorship
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences
import com.maksimowiczm.foodyou.sponsorship.domain.repository.SponsorRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class SponsorRepositoryImpl(
    private val sponsorshipDao: SponsorshipDao,
    private val networkDataSource: FoodYouSponsorsApiClient,
    private val preferences: UserPreferencesRepository<SponsorshipPreferences>,
    private val logger: Logger,
) : SponsorRepository {
    override fun observeSponsorships(fetchRemote: Boolean?): Flow<PagingData<Sponsorship>> =
        preferences.observe().flatMapLatest { prefs ->
            val useMediator =
                when {
                    fetchRemote == true -> {
                        logger.d(TAG) {
                            "User preferences overridden, allowing remote sponsorships"
                        }
                        true
                    }

                    fetchRemote == false -> {
                        logger.d(TAG) {
                            "User preferences overridden, disallowing remote sponsorships"
                        }
                        false
                    }

                    prefs.remoteAllowed -> {
                        logger.d(TAG) { "User preferences allow remote sponsorships" }
                        true
                    }

                    else -> {
                        logger.d(TAG) { "User preferences disallow remote sponsorships" }
                        false
                    }
                }

            val mediator =
                if (useMediator) {
                    SponsorshipRemoteMediator<Int, SponsorshipEntity>(
                        sponsorshipDao = sponsorshipDao,
                        networkDataSource = networkDataSource,
                    )
                } else {
                    null
                }

            Pager(
                    config = PagingConfig(pageSize = 20),
                    pagingSourceFactory = { sponsorshipDao.pagedFromLatest() },
                    remoteMediator = mediator,
                )
                .flow
                .map { data -> data.map { it.toModel() } }
        }

    private companion object {
        const val TAG = "SponsorRepositoryImpl"
    }
}

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
