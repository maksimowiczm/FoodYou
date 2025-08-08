package com.maksimowiczm.foodyou.navigation.domain

import kotlinx.serialization.Serializable

@Serializable internal data object HomeDestination : NavigationDestination

@Serializable internal data object HomeMasterDestination : NavigationDestination

@Serializable internal data object MealsCardsSettingsDestination : NavigationDestination

@Serializable internal data object GoalsCardSettingsDestination : NavigationDestination
