package com.maksimowiczm.foodyou.app.infrastructure.opensource

import com.maksimowiczm.foodyou.app.infrastructure.opensource.changelog.changelogModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.database.databaseModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.datastore.dataStoreModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.foodModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.foodDiaryModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.foodSearchModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.goals.goalsModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.importexport.importExportModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.network.networkModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.openFoodFactsModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.poll.pollModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.roomModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.settings.settingsModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.sponsorshipModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.USDAModule
import org.koin.dsl.module

val infrastructureOpenSourceModule = module {
    changelogModule()
    databaseModule()
    dataStoreModule()
    foodModule()
    foodDiaryModule()
    foodSearchModule()
    goalsModule()
    importExportModule()
    networkModule()
    openFoodFactsModule()
    pollModule()
    roomModule()
    settingsModule()
    sponsorshipModule()
    USDAModule()
}
