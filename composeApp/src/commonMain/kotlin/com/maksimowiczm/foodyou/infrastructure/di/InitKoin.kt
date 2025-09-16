package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.app.business.opensource.di.businessOpenSourceModule
import com.maksimowiczm.foodyou.app.business.shared.di.businessSharedModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.infrastructureOpenSourceModule
import com.maksimowiczm.foodyou.app.infrastructure.shared.infrastructureSharedModule
import com.maksimowiczm.foodyou.app.ui.changelog.uiChangelogModule
import com.maksimowiczm.foodyou.app.ui.database.opensource.uiDatabaseModule
import com.maksimowiczm.foodyou.app.ui.food.diary.add.uiFoodDiaryAddModule
import com.maksimowiczm.foodyou.app.ui.food.diary.quickadd.uiFoodDiaryQuickAddModule
import com.maksimowiczm.foodyou.app.ui.food.diary.search.uiFoodDiarySearchModule
import com.maksimowiczm.foodyou.app.ui.food.diary.update.uiFoodDiaryUpdateModule
import com.maksimowiczm.foodyou.app.ui.food.product.uiFoodProductModule
import com.maksimowiczm.foodyou.app.ui.food.recipe.uiFoodRecipeModule
import com.maksimowiczm.foodyou.app.ui.food.shared.uiFoodSharedModule
import com.maksimowiczm.foodyou.app.ui.goals.uiGoalsModule
import com.maksimowiczm.foodyou.app.ui.home.uiHomeModule
import com.maksimowiczm.foodyou.app.ui.language.uiLanguageModule
import com.maksimowiczm.foodyou.app.ui.meal.uiMealModule
import com.maksimowiczm.foodyou.app.ui.onboarding.opensource.uiOnboardingModule
import com.maksimowiczm.foodyou.app.ui.personalization.uiPersonalizationModule
import com.maksimowiczm.foodyou.app.ui.sponsor.uiSponsorModule
import com.maksimowiczm.foodyou.app.ui.theme.uiThemeModule
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes Koin with the provided configuration and modules.
 *
 * @param applicationCoroutineScope CoroutineScope with whole application lifecycle.
 * @param config Optional KoinAppDeclaration to configure Koin.
 */
fun initKoin(applicationCoroutineScope: CoroutineScope, config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)

        modules(appModule)

        // Infrastructure modules
        modules(
            infrastructureOpenSourceModule,
            infrastructureSharedModule(applicationCoroutineScope),
        )

        // Business modules
        modules(businessOpenSourceModule, businessSharedModule)

        modules(uiThemeModule)

        modules(uiSponsorModule)

        modules(uiChangelogModule)

        modules(uiLanguageModule)

        modules(uiPersonalizationModule)

        modules(uiOnboardingModule)

        modules(uiGoalsModule)

        modules(uiDatabaseModule)

        modules(uiMealModule)

        modules(uiHomeModule)

        modules(
            uiFoodDiaryAddModule,
            uiFoodDiaryQuickAddModule,
            uiFoodDiarySearchModule,
            uiFoodDiaryUpdateModule,
            uiFoodProductModule,
            uiFoodRecipeModule,
            uiFoodSharedModule,
        )
    }
