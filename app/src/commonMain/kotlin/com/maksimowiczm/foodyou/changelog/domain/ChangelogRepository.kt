package com.maksimowiczm.foodyou.changelog.domain

import kotlinx.coroutines.flow.Flow

interface ChangelogRepository {
    fun observe(): Flow<Changelog>
}
