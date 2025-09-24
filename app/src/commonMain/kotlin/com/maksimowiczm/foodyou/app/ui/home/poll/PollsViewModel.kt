package com.maksimowiczm.foodyou.app.ui.home.poll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.poll.domain.entity.PollId
import com.maksimowiczm.foodyou.poll.domain.entity.PollPreferences
import com.maksimowiczm.foodyou.poll.domain.usecase.ObserveActivePollUseCase
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
