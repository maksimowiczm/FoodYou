package com.maksimowiczm.foodyou.business.sponsorship.application.query

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.network.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.sponsorship.domain.Sponsorship
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network.RemoteSponsorshipDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.network.SponsorshipRemoteMediator
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.persistence.LocalSponsorshipDataSource
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.preferences.SponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Query to observe sponsorships.
 *
 * @param allowRemote If true, remote sponsorships will be fetched and observed. This will override
 *   the user's preferences. If false, only local sponsorships will be observed, regardless of the
 *   user's preferences. If null, the user's preferences will be respected.
 */
data class ObserveSponsorshipsQuery(val allowRemote: Boolean? = null) :
    Query<PagingData<Sponsorship>>

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
internal class ObserveSponsorshipsQueryHandler(
    private val localSponsorshipDataSource: LocalSponsorshipDataSource,
    private val remoteSponsorshipDataSource: RemoteSponsorshipDataSource,
    private val sponsorshipPreferencesDataSource: SponsorshipPreferencesDataSource,
) : QueryHandler<ObserveSponsorshipsQuery, PagingData<Sponsorship>> {
    override fun handle(query: ObserveSponsorshipsQuery): Flow<PagingData<Sponsorship>> =
        sponsorshipPreferencesDataSource.observe().flatMapLatest { prefs ->
            val (allowRemote) = query

            val mediatorFactory =
                when {
                    allowRemote == true -> {
                        FoodYouLogger.d(TAG) {
                            "User preferences overridden, allowing remote sponsorships"
                        }
                        remoteMediatorFactory()
                    }

                    allowRemote == false -> {
                        FoodYouLogger.d(TAG) {
                            "User preferences overridden, disallowing remote sponsorships"
                        }
                        null
                    }

                    prefs.remoteAllowed -> {
                        FoodYouLogger.d(TAG) { "User preferences allow remote sponsorships" }
                        remoteMediatorFactory()
                    }

                    else -> {
                        FoodYouLogger.d(TAG) { "User preferences disallow remote sponsorships" }
                        null
                    }
                }

            localSponsorshipDataSource.observeSponsorships(
                config = PagingConfig(pageSize = 20),
                remoteMediatorFactory = mediatorFactory,
            )
        }

    private fun remoteMediatorFactory(): RemoteMediatorFactory =
        object : RemoteMediatorFactory {
            override fun <K : Any, T : Any> create(): RemoteMediator<K, T>? =
                SponsorshipRemoteMediator(
                    localSponsorshipDataSource = localSponsorshipDataSource,
                    remoteSponsorshipDataSource = remoteSponsorshipDataSource,
                )
        }

    private companion object {
        const val TAG = "ObserveSponsorshipsQueryHandler"
    }
}
