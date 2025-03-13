package com.maksimowiczm.foodyou.feature.home.caloriescard

import androidx.navigation.NavController
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.home.HomeFeature
import com.maksimowiczm.foodyou.feature.home.caloriescard.ui.CaloriesCard
import com.maksimowiczm.foodyou.ui.DiaryViewModel
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class CaloriesCard(private val diaryRepository: Module.() -> KoinDefinition<DiaryRepository>) :
    Feature.Home {
    override fun KoinApplication.module() = module {
        viewModelOf(::DiaryViewModel)

        diaryRepository()
    }

    override fun build(navController: NavController) = HomeFeature { _, modifier, homeState ->
        CaloriesCard(
            homeState = homeState,
            modifier = modifier
        )
    }
}
