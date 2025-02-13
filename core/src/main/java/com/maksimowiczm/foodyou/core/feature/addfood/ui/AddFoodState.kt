package com.maksimowiczm.foodyou.core.feature.addfood.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchBottomBarState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchListState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchTopBarState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.rememberSearchBottomBarState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.rememberSearchListState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.rememberSearchTopBarState

@Composable
fun rememberAddFoodState(
    searchBarState: SearchTopBarState = rememberSearchTopBarState(),
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
            searchTopBarState = searchBarState,
            searchListState = searchListState,
            searchBottomBarState = searchBottomBarState,
            navController = navController
        )
    }
}

@Stable
class AddFoodState(
    val searchTopBarState: SearchTopBarState,
    val searchListState: SearchListState,
    val searchBottomBarState: SearchBottomBarState,
    val navController: NavHostController
)
