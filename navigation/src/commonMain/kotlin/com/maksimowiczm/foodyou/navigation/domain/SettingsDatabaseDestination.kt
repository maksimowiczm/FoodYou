package com.maksimowiczm.foodyou.navigation.domain

import kotlinx.serialization.Serializable

@Serializable internal data object SettingsDatabaseDestination : NavigationDestination

@Serializable internal data object SettingsDatabaseMasterDestination : NavigationDestination

@Serializable internal data object SettingsExternalDatabasesDestination : NavigationDestination

@Serializable internal data object UsdaApiKeyDestination : NavigationDestination

@Serializable internal data object DumpDatabaseDestination : NavigationDestination
