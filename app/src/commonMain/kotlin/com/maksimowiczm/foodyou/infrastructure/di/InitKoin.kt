package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.repository.repositoryModule
import com.maksimowiczm.foodyou.core.util.utilsModule
import com.maksimowiczm.foodyou.feature.about.aboutModule
import com.maksimowiczm.foodyou.feature.addfood.addFoodModule
import com.maksimowiczm.foodyou.feature.calendar.calendarModule
import com.maksimowiczm.foodyou.feature.goals.goalsModule
import com.maksimowiczm.foodyou.feature.language.languageModule
import com.maksimowiczm.foodyou.feature.meal.mealModule
import com.maksimowiczm.foodyou.feature.openfoodfacts.openFoodFactsSettingsModule
import com.maksimowiczm.foodyou.feature.product.productModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        databaseModule,
        dataStoreModule,
        repositoryModule,
        utilsModule
    )

    modules(
        aboutModule,
        addFoodModule,
        calendarModule,
        goalsModule,
        languageModule,
        mealModule,
        openFoodFactsSettingsModule,
        productModule
    )
}
