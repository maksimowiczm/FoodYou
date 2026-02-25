package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.food.search.favoritefood.FavoriteFoodSearchChip
import com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral.FoodDataCentralSearchChip
import com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts.OpenFoodFactsSearchChip
import com.maksimowiczm.foodyou.app.ui.food.search.userfood.UserFoodSearchChip

@Composable
internal fun FoodSearchFilters(
    selectedSource: FoodSource,
    onSource: (FoodSource) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
) {
    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalItemSpacing = 8.dp,
    ) {
        item {
            FavoriteFoodSearchChip(
                selected = selectedSource == FoodSource.Favorite,
                onSelect = { onSource(FoodSource.Favorite) },
                modifier = Modifier.animateItem(),
            )
        }
        item {
            UserFoodSearchChip(
                selected = selectedSource == FoodSource.YourFood,
                onSelect = { onSource(FoodSource.YourFood) },
                modifier = Modifier.animateItem(),
            )
        }
        item {
            OpenFoodFactsSearchChip(
                selected = selectedSource == FoodSource.OpenFoodFacts,
                onSelect = { onSource(FoodSource.OpenFoodFacts) },
                modifier = Modifier.animateItem(),
            )
        }
        item {
            FoodDataCentralSearchChip(
                selected = selectedSource == FoodSource.USDA,
                onSelect = { onSource(FoodSource.USDA) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}
