package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface SearchHintBuilder {
    fun NavGraphBuilder.graph(navController: NavController)
    fun build(navController: NavController): SearchHint
}

/**
 * A composable that will be displayed under the search bar in the search screen.
 *
 * @see SearchHome
 */
fun interface SearchHint {
    @Composable
    operator fun invoke(modifier: Modifier)
}
