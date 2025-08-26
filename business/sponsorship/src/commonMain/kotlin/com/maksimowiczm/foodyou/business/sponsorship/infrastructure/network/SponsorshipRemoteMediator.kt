package com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.sponsorship.domain.Sponsorship
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.persistence.LocalSponsorshipDataSource
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalPagingApi::class)
internal class SponsorshipRemoteMediator<K : Any, T : Any>(
    private val localSponsorshipDataSource: LocalSponsorshipDataSource,
    private val remoteSponsorshipDataSource: RemoteSponsorshipDataSource,
) : RemoteMediator<K, T>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    @OptIn(ExperimentalTime::class)
    override suspend fun load(loadType: LoadType, state: PagingState<K, T>): MediatorResult {
        return try {
            when (loadType) {
                LoadType.REFRESH -> {
                    FoodYouLogger.d(TAG) { "Refresh" }

                    // Initially get the latest sponsorships and download sponsorships after that
                    val sponsorship = localSponsorshipDataSource.getLatestSponsorship()

                    // If there are no sponsorships, fetch the latest ones from the API
                    val after = sponsorship?.dateTime?.toInstant(TimeZone.currentSystemDefault())

                    val response =
                        remoteSponsorshipDataSource.getSponsorships(
                            after = after,
                            size = state.config.pageSize,
                        )

                    if (response.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val entities = response.sponsorships.map { it.toSponsorship() }

                    localSponsorshipDataSource.upsertSponsorships(entities)

                    MediatorResult.Success(
                        endOfPaginationReached = response.sponsorships.size == response.totalSize
                    )
                }

                LoadType.PREPEND -> {
                    val first = localSponsorshipDataSource.getLatestSponsorship()

                    FoodYouLogger.d(TAG) { "Prepend, $first" }

                    if (first == null) {
                        FoodYouLogger.d(TAG) { "No items to prepend" }
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val after = first.dateTime.toInstant(TimeZone.currentSystemDefault())

                    val response =
                        remoteSponsorshipDataSource.getSponsorships(
                            after = after,
                            size = state.config.pageSize,
                        )
                    val entities = response.sponsorships.map { it.toSponsorship() }
                    localSponsorshipDataSource.upsertSponsorships(entities)

                    MediatorResult.Success(endOfPaginationReached = !response.hasMoreAfter)
                }

                LoadType.APPEND -> {
                    val last = localSponsorshipDataSource.getOldestSponsorship()

                    FoodYouLogger.d(TAG) { "Append, $last" }

                    val before = last?.dateTime?.toInstant(TimeZone.currentSystemDefault())

                    val response =
                        remoteSponsorshipDataSource.getSponsorships(
                            before = before,
                            size = state.config.pageSize,
                        )
                    val entities = response.sponsorships.map { it.toSponsorship() }
                    localSponsorshipDataSource.upsertSponsorships(entities)

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
private fun NetworkSponsorship.toSponsorship(): Sponsorship =
    Sponsorship(
        id = id,
        sponsorName = sponsor,
        message = message,
        amount = amount,
        currency = currency,
        inEuro = inEuro,
        dateTime = Instant.parse(sponsorshipDate).toLocalDateTime(TimeZone.currentSystemDefault()),
        method = method,
    )
