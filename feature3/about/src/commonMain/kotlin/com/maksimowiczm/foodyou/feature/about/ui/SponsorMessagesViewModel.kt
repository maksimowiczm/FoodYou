package com.maksimowiczm.foodyou.feature.about.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.about.data.database.AboutDatabase
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship
import com.maksimowiczm.foodyou.feature.about.data.network.SponsorshipApiClient
import com.maksimowiczm.foodyou.feature.about.data.network.SponsorshipsRemoteMediator
import com.maksimowiczm.foodyou.feature.about.preferences.SponsorsAllowed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class SponsorMessagesViewModel(
    private val database: AboutDatabase,
    private val sponsorshipApiClient: SponsorshipApiClient,
    dataStore: DataStore<Preferences>
) : ViewModel() {

    private val sponsorsAllowedPreference = dataStore.userPreference<SponsorsAllowed>()

    private val sponsorsOneTime = MutableStateFlow(false)

    private val _sponsorsAllowed = combine(
        sponsorsAllowedPreference.observe(),
        sponsorsOneTime
    ) { allowed, oneTime ->
        allowed || oneTime
    }

    val sponsorsAllowed = _sponsorsAllowed.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = runBlocking { _sponsorsAllowed.first() }
    )

    fun allowOnce() {
        sponsorsOneTime.value = true
    }

    fun allowAlways() {
        viewModelScope.launch {
            sponsorsAllowedPreference.set(true)
        }
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val sponsorshipPages: Flow<PagingData<Sponsorship>> = sponsorsAllowed.flatMapLatest { allowed ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true
            ),
            remoteMediator = if (allowed) {
                SponsorshipsRemoteMediator(
                    sponsorshipDao = database.sponsorshipDao,
                    sponsorshipApiClient = sponsorshipApiClient
                )
            } else {
                null
            }
        ) {
            database.sponsorshipDao.pagedFromLatest()
        }.flow.cachedIn(viewModelScope)
    }
}
