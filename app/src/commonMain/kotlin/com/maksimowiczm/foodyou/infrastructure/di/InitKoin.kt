package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.domain.domainModule
import com.maksimowiczm.foodyou.core.util.utilsModule
import com.maksimowiczm.foodyou.feature.about.aboutModule
import com.maksimowiczm.foodyou.feature.addfood.addFoodModule
import com.maksimowiczm.foodyou.feature.calendar.calendarModule
import com.maksimowiczm.foodyou.feature.goals.goalsModule
import com.maksimowiczm.foodyou.feature.importexport.importExportModule
import com.maksimowiczm.foodyou.feature.language.languageModule
import com.maksimowiczm.foodyou.feature.meal.mealModule
import com.maksimowiczm.foodyou.feature.measurement.measurementModule
import com.maksimowiczm.foodyou.feature.product.productModule
import com.maksimowiczm.foodyou.feature.recipe.recipeModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        databaseModule,
        dataStoreModule,
        domainModule,
        networkModule,
        utilsModule
    )

    modules(
        aboutModule,
        addFoodModule,
        calendarModule,
        goalsModule,
        importExportModule,
        languageModule,
        mealModule,
        measurementModule,
        productModule,
        recipeModule
    )
}
