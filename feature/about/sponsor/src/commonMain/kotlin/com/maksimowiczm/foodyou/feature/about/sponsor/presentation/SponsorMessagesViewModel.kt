package com.maksimowiczm.foodyou.feature.about.sponsor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import com.maksimowiczm.foodyou.business.sponsorship.application.command.AllowRemoteSponsorshipsCommand
import com.maksimowiczm.foodyou.business.sponsorship.application.query.ObserveSponsorshipPreferencesQuery
import com.maksimowiczm.foodyou.business.sponsorship.application.query.ObserveSponsorshipsQuery
import com.maksimowiczm.foodyou.business.sponsorship.domain.Sponsorship
import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorshipPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class SponsorMessagesViewModel(
    private val queryBus: QueryBus,
    private val commandBus: CommandBus,
) : ViewModel() {

    private val remoteAllowedOnce = MutableStateFlow(false)

    private val preferenceRemoteAllowedOnce =
        queryBus.dispatch<SponsorshipPreferences>(ObserveSponsorshipPreferencesQuery).map {
            it.remoteAllowed
        }

    private val _sponsorsAllowed =
        combine(preferenceRemoteAllowedOnce, remoteAllowedOnce.filterNotNull()) { always, oneTime ->
            always || oneTime
        }

    val sponsorsAllowed =
        _sponsorsAllowed.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { _sponsorsAllowed.first() },
        )

    fun allowOnce() {
        remoteAllowedOnce.value = true
    }

    fun allowAlways() {
        viewModelScope.launch { commandBus.dispatch<Unit, Unit>(AllowRemoteSponsorshipsCommand) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val sponsorshipPages: Flow<PagingData<Sponsorship>> =
        sponsorsAllowed
            .flatMapLatest { allowed ->
                queryBus.dispatch<PagingData<Sponsorship>>(
                    ObserveSponsorshipsQuery(allowRemote = allowed)
                )
            }
            .cachedIn(viewModelScope)
}
