package com.maksimowiczm.foodyou.app.infrastructure.opensource.changelog

import com.maksimowiczm.foodyou.app.business.shared.domain.changelog.ChangelogRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.changelogModule() {
    factoryOf(::ChangelogRepositoryImpl).bind<ChangelogRepository>()
}
