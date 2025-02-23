package com.maksimowiczm.foodyou.feature

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface NavigationFeature<Props> {
    fun NavGraphBuilder.graph(navController: NavController, props: Props)
}
