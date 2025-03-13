package com.maksimowiczm.foodyou.feature.settings.mealssettings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.MealsSettingsListItem
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.MealsSettingsScreen
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.MealsSettingsViewModel
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class MealsSettings(
    private val diaryRepository: Module.() -> KoinDefinition<DiaryRepository>,
    private val stringFormatRepository: Module.() -> KoinDefinition<StringFormatRepository>
) : Feature.Settings {
    override fun KoinApplication.module() = module {
        viewModelOf(::MealsSettingsViewModel)

        diaryRepository()
        stringFormatRepository()
    }

    override fun buildSettingsFeatures(navController: NavController) = SettingsFeature { modifier ->
        MealsSettingsListItem(
            onClick = {
                navController.navigate(
                    route = MealsSettings,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }

    @Serializable
    private data object MealsSettings

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<MealsSettings> {
            MealsSettingsScreen(
                onBack = {
                    navController.popBackStack<MealsSettings>(
                        inclusive = true
                    )
                }
            )
        }
    }
}
