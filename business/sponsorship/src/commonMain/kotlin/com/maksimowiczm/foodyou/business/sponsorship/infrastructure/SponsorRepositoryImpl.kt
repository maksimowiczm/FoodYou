package com.maksimowiczm.foodyou.business.sponsorship.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.sponsorship.SponsorshipEntity
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.room.RoomSponsorshipDataSource
import com.maksimowiczm.foodyou.core.shared.log.Logger
import com.maksimowiczm.foodyou.core.shared.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.core.sponsorship.domain.entity.Sponsorship
import com.maksimowiczm.foodyou.core.sponsorship.domain.entity.SponsorshipPreferences
import com.maksimowiczm.foodyou.core.sponsorship.domain.repository.SponsorRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class SponsorRepositoryImpl(
    private val localDataSource: RoomSponsorshipDataSource,
    private val networkDataSource: FoodYouSponsorsApiClient,
    private val preferences: UserPreferencesRepository<SponsorshipPreferences>,
    private val logger: Logger,
) : SponsorRepository {
    override fun observeSponsorships(fetchRemote: Boolean?): Flow<PagingData<Sponsorship>> =
        preferences.observe().flatMapLatest { prefs ->
            val mediatorFactory =
                when {
                    fetchRemote == true -> {
                        logger.d(TAG) {
                            "User preferences overridden, allowing remote sponsorships"
                        }
                        remoteMediatorFactory()
                    }

                    fetchRemote == false -> {
                        logger.d(TAG) {
                            "User preferences overridden, disallowing remote sponsorships"
                        }
                        null
                    }

                    prefs.remoteAllowed -> {
                        logger.d(TAG) { "User preferences allow remote sponsorships" }
                        remoteMediatorFactory()
                    }

                    else -> {
                        logger.d(TAG) { "User preferences disallow remote sponsorships" }
                        null
                    }
                }

            localDataSource.observeSponsorships(
                config = PagingConfig(pageSize = 20),
                remoteMediatorFactory = mediatorFactory,
            )
        }

    private fun remoteMediatorFactory(): () -> RemoteMediator<Int, SponsorshipEntity> = {
        SponsorshipRemoteMediator(
            localDataSource = localDataSource,
            networkDataSource = networkDataSource,
        )
    }

    private companion object {
        const val TAG = "SponsorRepositoryImpl"
    }
}
