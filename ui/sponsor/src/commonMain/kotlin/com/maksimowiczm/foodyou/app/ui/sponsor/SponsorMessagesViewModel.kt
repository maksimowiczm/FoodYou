package com.maksimowiczm.foodyou.app.ui.sponsor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Sponsorship
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences
import com.maksimowiczm.foodyou.sponsorship.domain.repository.SponsorRepository
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
    private val sponsorRepository: SponsorRepository,
    private val preferencesRepository: UserPreferencesRepository<SponsorshipPreferences>,
) : ViewModel() {

    private val remoteAllowedOnce = MutableStateFlow(false)

    private val preferenceRemoteAllowedOnce =
        preferencesRepository.observe().map { it.remoteAllowed }

    private val _sponsorsAllowed =
        combine(preferenceRemoteAllowedOnce, remoteAllowedOnce.filterNotNull()) { always, oneTime ->
            always || oneTime
        }

    val sponsorsAllowed =
        _sponsorsAllowed.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(2_000),
            initialValue = runBlocking { _sponsorsAllowed.first() },
        )

    fun allowOnce() {
        remoteAllowedOnce.value = true
    }

    fun allowAlways() {
        viewModelScope.launch { preferencesRepository.update { copy(remoteAllowed = true) } }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val sponsorshipPages: Flow<PagingData<Sponsorship>> =
        sponsorsAllowed
            .flatMapLatest(sponsorRepository::observeSponsorships)
            .cachedIn(viewModelScope)
}
