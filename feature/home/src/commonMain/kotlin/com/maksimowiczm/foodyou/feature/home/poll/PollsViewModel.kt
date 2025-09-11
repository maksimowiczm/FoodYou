package com.maksimowiczm.foodyou.feature.home.poll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.ObserveActivePollUseCase
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.PollId
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.PollPreferences
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class PollsViewModel(
    observeActivePollUseCase: ObserveActivePollUseCase,
    private val pollPreferencesRepository: UserPreferencesRepository<PollPreferences>,
) : ViewModel() {
    val polls =
        observeActivePollUseCase
            .observe()
            .stateIn(
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(5_000),
                scope = viewModelScope,
            )

    fun dismissPoll(pollId: PollId) {
        viewModelScope.launch {
            pollPreferencesRepository.update { copy(dismissedPolls = dismissedPolls + pollId) }
        }
    }
}
