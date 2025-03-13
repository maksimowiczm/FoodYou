package com.maksimowiczm.foodyou.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.feature.home.HomeFeature
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import org.koin.core.component.KoinComponent

/**
 * A feature is a collection of related functionality that can be added to the application.
 */
sealed interface Feature {

    /**
     * Navigation graph that will be added to the main navigation graph.
     *
     * @param navController The main navigation controller.
     */
    fun NavGraphBuilder.graph(navController: NavController) = Unit

    /**
     * Initialize the feature. This will run before UI is displayed and block the main thread.
     * Should be fast and not perform any heavy operations.
     */
    suspend fun KoinComponent.initialize() = Unit

    /**
     * A feature that can be added to the home screen.
     */
    interface Home : Feature {
        /**
         * Home feature that will be added to the home screen.
         *
         * @param navController The main navigation controller.
         */
        fun build(navController: NavController): HomeFeature
    }

    /**
     * A feature that can be added to the settings screen.
     */
    interface Settings : Feature {
        /**
         * List of settings features that will be added to the settings screen.
         *
         * @param navController The main navigation controller.
         */
        fun buildSettingsFeatures(navController: NavController): SettingsFeature
    }
}
