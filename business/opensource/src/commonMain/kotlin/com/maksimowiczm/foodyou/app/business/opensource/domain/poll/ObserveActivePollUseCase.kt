package com.maksimowiczm.foodyou.app.business.opensource.domain.poll

import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.AppLaunchInfo
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.Settings
import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
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
    private fun AppLaunchInfo.canShowPolls(): Boolean =
        if (firstLaunch == null || launchesCount < 10) {
            false
        } else {
            firstLaunch.plus(3.days) < dateProvider.nowInstant()
        }

    private fun activePollsFlow(): Flow<List<Poll>> =
        combine(pollPreferencesRepository.observe(), pollRepository.observeActivePolls()) {
            pollPreferences,
            activePolls ->
            activePolls.filterNot { poll -> poll.id in pollPreferences.dismissedPolls }
        }
}
