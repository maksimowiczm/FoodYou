package com.maksimowiczm.foodyou.core.feature.addfood.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchBottomBarState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchListState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.rememberSearchBottomBarState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.rememberSearchListState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar.SearchBarState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar.rememberSearchBarState

@Composable
fun rememberAddFoodState(
    searchBarState: SearchBarState = rememberSearchBarState(),
    searchListState: SearchListState = rememberSearchListState(),
    searchBottomBarState: SearchBottomBarState = rememberSearchBottomBarState(),
    navController: NavHostController = rememberNavController()
): AddFoodState {
    return remember(
        searchBarState,
        searchListState,
        searchBottomBarState,
        navController
    ) {
        AddFoodState(
            searchBarState = searchBarState,
            searchListState = searchListState,
            searchBottomBarState = searchBottomBarState,
            navController = navController
        )
    }
}

@Immutable
class AddFoodState(
    val searchBarState: SearchBarState,
    val searchListState: SearchListState,
    val searchBottomBarState: SearchBottomBarState,
    val navController: NavHostController
)
