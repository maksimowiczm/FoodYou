package com.maksimowiczm.foodyou.changelog.infrastructure

import com.maksimowiczm.foodyou.changelog.domain.Changelog
import com.maksimowiczm.foodyou.changelog.domain.ChangelogRepository
import com.maksimowiczm.foodyou.common.config.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class ChangelogRepositoryImpl(private val appConfig: AppConfig) : ChangelogRepository {
    override fun observe(): Flow<Changelog> = flowOf(StaticChangelog(appConfig))
}
