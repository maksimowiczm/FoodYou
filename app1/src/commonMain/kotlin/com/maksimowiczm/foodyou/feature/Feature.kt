package com.maksimowiczm.foodyou.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * A feature is a collection of related functionality that can be added to the application.
 */
interface Feature {

    /**
     * Navigation graph that will be added to the main navigation graph.
     *
     * @param navController The main navigation controller.
     */
    fun NavGraphBuilder.graph(navController: NavController) = Unit

    /**
     * Koin modules that will be added to the main Koin application.
     */
    val module: Module
        get() = module {}

    /**
     * Initialize the feature. This will run before UI is displayed and block the main thread.
     * Should be fast and not perform any heavy operations.
     */
    suspend fun KoinComponent.initialize() = Unit

    /**
     * Home features that will be added to the home screen.
     *
     * @param navController The main navigation controller.
     */
    fun buildHomeFeatures(navController: NavController): List<HomeFeature> = emptyList()

    /**
     * List of settings features that will be added to the settings screen.
     *
     * @param navController The main navigation controller.
     */
    fun buildSettingsFeatures(navController: NavController): List<SettingsFeature> = emptyList()

    /**
     * Single home feature
     */
    abstract class Home : Feature {
        abstract fun build(navController: NavController): HomeFeature

        final override fun buildHomeFeatures(navController: NavController) =
            listOf(build(navController))
    }

    /**
     * Single settings feature
     */
    abstract class Settings : Feature {
        abstract fun build(navController: NavController): SettingsFeature

        final override fun buildSettingsFeatures(navController: NavController) =
            listOf(build(navController))
    }
}
