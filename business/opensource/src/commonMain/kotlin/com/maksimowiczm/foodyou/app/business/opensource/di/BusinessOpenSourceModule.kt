package com.maksimowiczm.foodyou.app.business.opensource.di

import com.maksimowiczm.foodyou.app.business.opensource.domain.changelog.changelogModule
import com.maksimowiczm.foodyou.app.business.opensource.domain.fooddiary.foodDiaryModule
import com.maksimowiczm.foodyou.app.business.opensource.domain.importexport.importExportModule
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.pollModule
import com.maksimowiczm.foodyou.app.business.opensource.domain.search.searchModule
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.settingsModule
import org.koin.dsl.module

val businessOpenSourceModule = module {
    includeCoreUseCases()
    changelogModule()
    foodDiaryModule()
    importExportModule()
    pollModule()
    searchModule()
    settingsModule()
}
