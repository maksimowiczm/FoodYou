package com.maksimowiczm.foodyou.feature.about.data.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship
import com.maksimowiczm.foodyou.feature.about.data.database.SponsorshipDao
import com.maksimowiczm.foodyou.feature.about.data.database.SponsorshipMethod
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalPagingApi::class)
internal class SponsorshipsRemoteMediator(
    private val sponsorshipDao: SponsorshipDao,
    private val sponsorshipApiClient: SponsorshipApiClient
) : RemoteMediator<Int, Sponsorship>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    @OptIn(ExperimentalTime::class)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Sponsorship>
    ): MediatorResult {
        return try {
            when (loadType) {
                LoadType.REFRESH -> {
                    Logger.d(TAG) { "Refresh" }

                    // Initially get the latest sponsorships and download sponsorships after that
                    val sponsorship = sponsorshipDao.getLatestSponsorship()

                    // If there are no sponsorships, fetch the latest ones from the API
                    val after = if (sponsorship == null) {
                        null
                    } else {
                        Instant.fromEpochSeconds(sponsorship.sponsorshipEpochSeconds)
                    }

                    val response = sponsorshipApiClient.getSponsorships(
                        after = after,
                        size = state.config.pageSize
                    )

                    if (response.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val entities = response.sponsorships.map { it.toSponsorship() }

                    sponsorshipDao.upsert(entities)

                    MediatorResult.Success(
                        endOfPaginationReached = response.sponsorships.size == response.totalSize
                    )
                }

                LoadType.PREPEND -> {
                    val first = sponsorshipDao.getLatestSponsorship()

                    Logger.d(TAG) { "Prepend, $first" }

                    if (first == null) {
                        Logger.d(TAG) { "No items to prepend" }
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val after = Instant.fromEpochSeconds(first.sponsorshipEpochSeconds)

                    val response = sponsorshipApiClient.getSponsorships(
                        after = after,
                        size = state.config.pageSize
                    )
                    val entities = response.sponsorships.map { it.toSponsorship() }
                    sponsorshipDao.upsert(entities)

                    MediatorResult.Success(
                        endOfPaginationReached = !response.hasMoreAfter
                    )
                }

                LoadType.APPEND -> {
                    val last = sponsorshipDao.getOldestSponsorship()

                    Logger.d(TAG) { "Append, $last" }

                    val before = if (last == null) {
                        null
                    } else {
                        Instant.fromEpochSeconds(last.sponsorshipEpochSeconds)
                    }

                    val response = sponsorshipApiClient.getSponsorships(
                        before = before,
                        size = state.config.pageSize
                    )
                    val entities = response.sponsorships.map { it.toSponsorship() }
                    sponsorshipDao.upsert(entities)

                    MediatorResult.Success(
                        endOfPaginationReached = !response.hasMoreBefore
                    )
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.w(TAG, e)
            MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "SponsorshipsRemoteMediator"
    }
}

@OptIn(ExperimentalTime::class)
private fun NetworkSponsorship.toSponsorship(): Sponsorship = Sponsorship(
    id = id,
    sponsorName = sponsor,
    message = message,
    amount = amount,
    currency = currency,
    inEuro = inEuro,
    sponsorshipEpochSeconds = Instant.parse(sponsorshipDate).epochSeconds,
    method = when (method.lowercase()) {
        "ko-fi" -> SponsorshipMethod.Kofi
        "liberapay" -> SponsorshipMethod.Liberapay
        "crypto" -> SponsorshipMethod.Crypto
        else -> error("Unknown sponsorship method: $method")
    }
)
