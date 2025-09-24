package com.maksimowiczm.foodyou.changelog

import com.maksimowiczm.foodyou.changelog.domain.ChangelogRepository
import com.maksimowiczm.foodyou.changelog.infrastructure.ChangelogRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val changelogModule = module { factoryOf(::ChangelogRepositoryImpl).bind<ChangelogRepository>() }
