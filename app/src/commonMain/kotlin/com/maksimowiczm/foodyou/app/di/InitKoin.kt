package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.app.ui.uiModule
import com.maksimowiczm.foodyou.changelog.changelogModule
import com.maksimowiczm.foodyou.food.foodModule
import com.maksimowiczm.foodyou.food.search.foodSearchModule
import com.maksimowiczm.foodyou.fooddiary.foodDiaryModule
import com.maksimowiczm.foodyou.goals.goalsModule
import com.maksimowiczm.foodyou.importexport.importExportModule
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.importExportSwissFoodCompositionDatabaseModule
import com.maksimowiczm.foodyou.importexport.tbca.importExportTBCAModule
import com.maksimowiczm.foodyou.poll.pollModule
import com.maksimowiczm.foodyou.settings.settingsModule
import com.maksimowiczm.foodyou.sponsorship.sponsorshipModule
import com.maksimowiczm.foodyou.theme.themeModule
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(applicationCoroutineScope: CoroutineScope, config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)

        modules(appModule(applicationCoroutineScope))
        modules(uiModule)
        modules(
            changelogModule,
            foodModule,
            foodSearchModule,
            foodDiaryModule,
            goalsModule,
            importExportModule,
            importExportSwissFoodCompositionDatabaseModule,
            importExportTBCAModule,
            pollModule,
            settingsModule,
            sponsorshipModule,
            themeModule,
        )
    }
