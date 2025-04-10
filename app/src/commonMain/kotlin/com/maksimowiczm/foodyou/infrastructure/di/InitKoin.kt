package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.coreModule
import com.maksimowiczm.foodyou.feature.about.aboutModule
import com.maksimowiczm.foodyou.feature.addfood.addFoodModule
import com.maksimowiczm.foodyou.feature.calendar.calendarModule
import com.maksimowiczm.foodyou.feature.language.languageModule
import com.maksimowiczm.foodyou.feature.meal.mealModule
import com.maksimowiczm.foodyou.feature.measurement.measurementModule
import com.maksimowiczm.foodyou.feature.openfoodfacts.openFoodFactsSettingsModule
import com.maksimowiczm.foodyou.feature.product.productModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        databaseModule,
        dataStoreModule
    )

    modules(
        aboutModule,
        addFoodModule,
        calendarModule,
        languageModule,
        mealModule,
        measurementModule,
        openFoodFactsSettingsModule,
        productModule
    )
}
