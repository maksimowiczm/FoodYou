package com.maksimowiczm.foodyou.feature.settings.goalssettings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.feature.settings.goalssettings.ui.GoalsSettingsListItem
import com.maksimowiczm.foodyou.feature.settings.goalssettings.ui.GoalsSettingsScreen
import com.maksimowiczm.foodyou.feature.settings.goalssettings.ui.GoalsSettingsViewModel
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class GoalsSettings(private val diaryRepository: Module.() -> KoinDefinition<DiaryRepository>) :
    Feature.Settings {
    override fun KoinApplication.module() = module {
        viewModelOf(::GoalsSettingsViewModel)

        diaryRepository()
    }

    override fun buildSettingsFeatures(navController: NavController) = SettingsFeature { modifier ->
        GoalsSettingsListItem(
            onGoalsClick = {
                navController.navigate(
                    route = GoalsSettings,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }

    @Serializable
    private data object GoalsSettings

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<GoalsSettings> {
            GoalsSettingsScreen(
                onBack = {
                    navController.popBackStack(
                        route = GoalsSettings,
                        inclusive = true
                    )
                }
            )
        }
    }
}
