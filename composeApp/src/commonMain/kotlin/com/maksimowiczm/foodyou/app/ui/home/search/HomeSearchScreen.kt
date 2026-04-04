package com.maksimowiczm.foodyou.app.ui.home.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchApp
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchApp
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchApp
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchApp
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
internal fun HomeSearchScreen(
    contentPadding: PaddingValues,
    homeSearchState: HomeSearchState,
    onFoodDataCentralProduct: (FoodDataCentralProductIdentity) -> Unit,
    onOpenFoodFactsProduct: (OpenFoodFactsProductIdentity) -> Unit,
    onUserFood: (UserProductIdentity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shimmer = rememberShimmer(ShimmerBounds.View)

    // We need stable collection
    val derivedCollection by
        remember(homeSearchState) { derivedStateOf { homeSearchState.collection } }

    when (derivedCollection) {
        is FavoriteCollectionFilter ->
            FavoriteFoodSearchApp(
                shimmer = shimmer,
                contentPadding = contentPadding,
                lazyListState = homeSearchState.listStates.favorite,
                onClick = {
                    when (it) {
                        is UserProduct -> onUserFood(it.identity)
                        is OpenFoodFactsProduct -> onOpenFoodFactsProduct(it.identity)
                        is FoodDataCentralProduct -> onFoodDataCentralProduct(it.identity)
                        else -> error("Unknown type ${it::class}")
                    }
                },
                modifier = modifier,
            )

        is FoodDataCentralCollectionFilter ->
            FoodDataCentralSearchApp(
                shimmer = shimmer,
                contentPadding = contentPadding,
                lazyListState = homeSearchState.listStates.foodDataCentral,
                onClick = { onFoodDataCentralProduct(it.identity) },
                modifier = modifier,
            )

        is OpenFoodFactsCollectionFilter ->
            OpenFoodFactsSearchApp(
                shimmer = shimmer,
                contentPadding = contentPadding,
                lazyListState = homeSearchState.listStates.openFoodFacts,
                onClick = { onOpenFoodFactsProduct(it.identity) },
                modifier = modifier,
            )

        is YourFoodCollectionFilter ->
            UserFoodSearchApp(
                shimmer = shimmer,
                contentPadding = contentPadding,
                lazyListState = homeSearchState.listStates.userFood,
                onUserProduct = { onUserFood(it.identity) },
                modifier = modifier,
            )
    }
}
