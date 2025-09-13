package com.maksimowiczm.foodyou.app.business.shared.domain.changelog

import kotlinx.coroutines.flow.Flow

interface ChangelogRepository {
    fun observe(): Flow<Changelog>
}
