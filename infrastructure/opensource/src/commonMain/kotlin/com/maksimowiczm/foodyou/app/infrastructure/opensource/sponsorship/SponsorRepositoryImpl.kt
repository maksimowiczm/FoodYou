package com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship

import com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.foodyousponsors.NetworkSponsorship
import com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.room.SponsorshipDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.room.SponsorshipEntity
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.shared.domain.userpreferences.get
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Sponsorship
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences
import com.maksimowiczm.foodyou.sponsorship.domain.repository.SponsorRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
internal class SponsorRepositoryImpl(
    private val sponsorshipDao: SponsorshipDao,
    private val networkDataSource: FoodYouSponsorsApiClient,
    private val preferences: UserPreferencesRepository<SponsorshipPreferences>,
    private val rateLimiter: SponsorRateLimiter,
    private val logger: Logger,
) : SponsorRepository {

    override suspend fun requestSync(yearMonth: YearMonth) {
        val prefs = preferences.get()
        if (!prefs.remoteAllowed) {
            logger.d(TAG) { "Remote data fetching is disabled by user preferences." }
            return
        }

        if (!rateLimiter.canMakeRequest(yearMonth)) {
            logger.d(TAG) { "Rate limiter is preventing a new request for $yearMonth" }
            return
        }

        logger.d(TAG) { "Requesting sponsorship sync for $yearMonth" }
        rateLimiter.recordRequest(yearMonth)
        val sponsorships = networkDataSource.getSponsorships(yearMonth)
        val entities = sponsorships.map(NetworkSponsorship::toEntity)
        sponsorshipDao.upsert(entities)
    }

    @OptIn(ExperimentalTime::class)
    override fun observeByYearMonth(yearMonth: YearMonth): Flow<List<Sponsorship>> {
        val fromEpochSeconds = yearMonth.firstDay.atStartOfDayIn(TimeZone.UTC).epochSeconds
        val toEpochSeconds = yearMonth.lastDay.atStartOfDayIn(TimeZone.UTC).epochSeconds + 86_399

        return sponsorshipDao.observeDateBetween(fromEpochSeconds, toEpochSeconds).map { list ->
            list.map { it.toModel() }
        }
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

@OptIn(ExperimentalTime::class)
private fun NetworkSponsorship.toEntity(): SponsorshipEntity =
    SponsorshipEntity(
        id = id,
        sponsorName = sponsor,
        message = message,
        amount = amount,
        currency = currency,
        inEuro = inEuro,
        sponsorshipEpochSeconds = Instant.parse(sponsorshipDate).epochSeconds,
        method = method,
    )
