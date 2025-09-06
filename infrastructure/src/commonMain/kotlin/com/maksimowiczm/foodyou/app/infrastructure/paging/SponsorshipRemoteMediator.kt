package com.maksimowiczm.foodyou.app.infrastructure.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.app.infrastructure.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.app.infrastructure.foodyousponsors.NetworkSponsorship
import com.maksimowiczm.foodyou.app.infrastructure.room.sponsorship.SponsorshipDao
import com.maksimowiczm.foodyou.app.infrastructure.room.sponsorship.SponsorshipEntity
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalPagingApi::class)
internal class SponsorshipRemoteMediator<K : Any, T : Any>(
    private val sponsorshipDao: SponsorshipDao,
    private val networkDataSource: FoodYouSponsorsApiClient,
) : RemoteMediator<K, T>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    @OptIn(ExperimentalTime::class)
    override suspend fun load(loadType: LoadType, state: PagingState<K, T>): MediatorResult {
        return try {
            when (loadType) {
                LoadType.REFRESH -> {
                    FoodYouLogger.d(TAG) { "Refresh" }

                    // Initially get the latest sponsorships and download sponsorships after that
                    val sponsorship = sponsorshipDao.getLatestSponsorship()

                    // If there are no sponsorships, fetch the latest ones from the API
                    val after = sponsorship?.sponsorshipEpochSeconds?.let(Instant::fromEpochSeconds)

                    val response =
                        networkDataSource.getSponsorships(
                            after = after,
                            size = state.config.pageSize,
                        )

                    if (response.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val entities = response.sponsorships.map(NetworkSponsorship::toEntity)

                    sponsorshipDao.upsert(entities)

                    MediatorResult.Success(
                        endOfPaginationReached = response.sponsorships.size == response.totalSize
                    )
                }

                LoadType.PREPEND -> {
                    val first = sponsorshipDao.getLatestSponsorship()

                    FoodYouLogger.d(TAG) { "Prepend, $first" }

                    if (first == null) {
                        FoodYouLogger.d(TAG) { "No items to prepend" }
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val after = first.sponsorshipEpochSeconds.let(Instant::fromEpochSeconds)

                    val response =
                        networkDataSource.getSponsorships(
                            after = after,
                            size = state.config.pageSize,
                        )
                    val entities = response.sponsorships.map(NetworkSponsorship::toEntity)
                    sponsorshipDao.upsert(entities)

                    MediatorResult.Success(endOfPaginationReached = !response.hasMoreAfter)
                }

                LoadType.APPEND -> {
                    val last = sponsorshipDao.getOldestSponsorship()

                    FoodYouLogger.d(TAG) { "Append, $last" }

                    val before = last?.sponsorshipEpochSeconds?.let(Instant::fromEpochSeconds)

                    val response =
                        networkDataSource.getSponsorships(
                            before = before,
                            size = state.config.pageSize,
                        )
                    val entities = response.sponsorships.map(NetworkSponsorship::toEntity)
                    sponsorshipDao.upsert(entities)

                    MediatorResult.Success(endOfPaginationReached = !response.hasMoreBefore)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FoodYouLogger.w(TAG, e) { "Error loading sponsorships" }
            MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "SponsorshipRemoteMediator"
    }
}

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
