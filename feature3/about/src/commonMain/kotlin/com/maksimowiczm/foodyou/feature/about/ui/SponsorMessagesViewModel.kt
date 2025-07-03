package com.maksimowiczm.foodyou.feature.about.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.about.data.database.AboutDatabase
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship
import com.maksimowiczm.foodyou.feature.about.data.network.SponsorshipApiClient
import com.maksimowiczm.foodyou.feature.about.data.network.SponsorshipsRemoteMediator
import kotlinx.coroutines.flow.Flow

internal class SponsorMessagesViewModel(
    private val database: AboutDatabase,
    private val sponsorshipApiClient: SponsorshipApiClient
) : ViewModel() {

    @OptIn(ExperimentalPagingApi::class)
    val sponsorshipPages: Flow<PagingData<Sponsorship>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true
        ),
        remoteMediator = SponsorshipsRemoteMediator(
            sponsorshipDao = database.sponsorshipDao,
            sponsorshipApiClient = sponsorshipApiClient
        )
    ) {
        database.sponsorshipDao.pagedFromLatest()
    }.flow.cachedIn(viewModelScope)
}
