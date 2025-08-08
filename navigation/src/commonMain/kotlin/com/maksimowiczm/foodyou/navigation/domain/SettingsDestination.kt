package com.maksimowiczm.foodyou.navigation.domain

import kotlinx.serialization.Serializable

@Serializable internal data object SettingsDestination : NavigationDestination

@Serializable internal data object SettingsMasterDestination : NavigationDestination

@Serializable internal data object SettingsMealsDestination : NavigationDestination

@Serializable internal data object SettingsLanguageDestination : NavigationDestination

@Serializable internal data object SettingsGoalsDestination : NavigationDestination

@Serializable internal data object SettingsPersonalizationDestination : NavigationDestination

@Serializable internal data object SettingsNutritionFactsDestination : NavigationDestination

@Serializable internal data object SettingsHomeDestination : NavigationDestination

@Serializable internal data object SettingsDatabaseDestination : NavigationDestination
