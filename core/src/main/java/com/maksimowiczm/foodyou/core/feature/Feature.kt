package com.maksimowiczm.foodyou.core.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import org.koin.core.KoinApplication

/**
 * A feature is a collection of related functionality that can be added to the application.
 */
sealed interface Feature {

    /**
     * A feature that can be added to the koin application.
     */
    interface Koin : Feature {
        fun KoinApplication.setup()
    }

    /**
     * A feature that can be added to the home screen.
     */
    interface Home : Feature {

        /**
         * Navigation graph that will be added to the main navigation graph.
         *
         * @param navController The main navigation controller.
         */
        fun NavGraphBuilder.homeGraph(navController: NavController)

        /**
         * List of home features that will be added to the home screen.
         *
         * @param navController The main navigation controller.
         */
        fun buildHomeFeatures(navController: NavController): List<HomeFeature>
    }

    /**
     * A feature that can be added to the settings screen.
     */
    interface Settings : Feature {

        /**
         * Navigation graph that will be added to the main navigation graph.
         *
         * @param navController The main navigation controller.
         */
        fun NavGraphBuilder.settingsGraph(navController: NavController)

        /**
         * List of settings features that will be added to the settings screen.
         *
         * @param navController The main navigation controller.
         */
        fun buildSettingsFeatures(navController: NavController): List<SettingsFeature>
    }
}
