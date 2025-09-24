package com.maksimowiczm.foodyou.poll.domain.usecase

import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.poll.domain.entity.Poll
import com.maksimowiczm.foodyou.poll.domain.entity.PollPreferences
import com.maksimowiczm.foodyou.poll.domain.repository.PollRepository
import com.maksimowiczm.foodyou.settings.domain.entity.AppLaunchInfo
import com.maksimowiczm.foodyou.settings.domain.entity.Settings
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveActivePollUseCase(
    private val settingsRepository: UserPreferencesRepository<Settings>,
    private val pollPreferencesRepository: UserPreferencesRepository<PollPreferences>,
    private val pollRepository: PollRepository,
    private val dateProvider: DateProvider,
) {
    fun observe(): Flow<List<Poll>> =
        settingsRepository
            .observe()
            .map { it.appLaunchInfo.canShowPolls() }
            .flatMapLatest { canShow ->
                if (!canShow) {
                    flowOf(listOf())
                } else {
                    activePollsFlow()
                }
            }

    // Show polls only if first launch was more than 3 days ago and user launched app at least
    // 10 times
    private fun AppLaunchInfo.canShowPolls(): Boolean {
        val firstLaunch = firstLaunch

        return if (firstLaunch == null || launchesCount < 10) {
            false
        } else {
            firstLaunch.plus(3.days) < dateProvider.nowInstant()
        }
    }

    private fun activePollsFlow(): Flow<List<Poll>> =
        combine(pollPreferencesRepository.observe(), pollRepository.observeActivePolls()) {
            pollPreferences,
            activePolls ->
            activePolls.filterNot { poll -> poll.id in pollPreferences.dismissedPolls }
        }
}
