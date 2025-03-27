package com.maksimowiczm.foodyou.feature.diary

import androidx.navigation.NavController
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveMealsByDateUseCase
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.mealscard.buildMealsCard
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.binds
import org.koin.dsl.module

object DiaryFeature : Feature {
    override fun buildHomeFeatures(navController: NavController) = listOf(
        buildMealsCard(
            onMealClick = { epochDay, meal ->
            },
            onAddClick = { epochDay, meal ->
            }
        )
    )

    override fun declare(): KoinAppDeclaration = {
        modules(
            module {
                factoryOf(::DiaryRepository).binds(
                    arrayOf(
                        ObserveMealsByDateUseCase::class
                    )
                )

                viewModelOf(::MealsCardViewModel)
            }
        )
    }
}
