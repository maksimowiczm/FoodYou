package com.maksimowiczm.foodyou.business.sponsorship.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.shared.application.network.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorRepository
import com.maksimowiczm.foodyou.business.sponsorship.domain.Sponsorship
import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorshipPreferences
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.datastore.DataStoreSponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.room.RoomSponsorshipDataSource
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class SponsorRepositoryImpl(
    private val localDataSource: RoomSponsorshipDataSource,
    private val networkDataSource: FoodYouSponsorsApiClient,
    private val preferencesDataSource: DataStoreSponsorshipPreferencesDataSource,
    private val logger: Logger,
) : SponsorRepository {
    override fun observeSponsorships(allowRemote: Boolean?): Flow<PagingData<Sponsorship>> =
        preferencesDataSource.observe().flatMapLatest { prefs ->
            val mediatorFactory =
                when {
                    allowRemote == true -> {
                        logger.d(TAG) {
                            "User preferences overridden, allowing remote sponsorships"
                        }
                        remoteMediatorFactory()
                    }

                    allowRemote == false -> {
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

    private fun remoteMediatorFactory(): RemoteMediatorFactory =
        object : RemoteMediatorFactory {
            override fun <K : Any, T : Any> create(): RemoteMediator<K, T>? =
                SponsorshipRemoteMediator(
                    localDataSource = localDataSource,
                    networkDataSource = networkDataSource,
                )
        }

    override fun observeSponsorshipPreferences(): Flow<SponsorshipPreferences> =
        preferencesDataSource.observe()

    override suspend fun setSponsorshipPreferences(prefs: SponsorshipPreferences) {
        preferencesDataSource.update(prefs)
    }

    private companion object {
        const val TAG = "SponsorRepositoryImpl"
    }
}
