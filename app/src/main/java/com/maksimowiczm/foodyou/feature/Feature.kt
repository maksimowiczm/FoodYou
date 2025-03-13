package com.maksimowiczm.foodyou.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.feature.home.HomeFeature
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature

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
