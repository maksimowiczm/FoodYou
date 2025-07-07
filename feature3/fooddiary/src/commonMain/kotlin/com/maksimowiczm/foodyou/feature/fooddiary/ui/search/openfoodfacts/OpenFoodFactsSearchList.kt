package com.maksimowiczm.foodyou.feature.fooddiary.ui.search.openfoodfacts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.neutral_no_food_found
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OpenFoodFactsSearchList(
    pages: LazyPagingItems<OpenFoodFactsProduct>,
    contentPadding: PaddingValues,
    onClick: (OpenFoodFactsProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        if (pages.itemCount == 0) {
            Text(
                text = stringResource(Res.string.neutral_no_food_found),
                modifier = Modifier
                    .safeContentPadding()
                    .align(Alignment.Center)
            )
        }

        LazyColumn(
            contentPadding = contentPadding
        ) {
            items(
                count = pages.itemCount,
                key = pages.itemKey { it.id }
            ) {
                val product = pages[it]

                if (product != null) {
                    OpenFoodFactsSearchListItem(
                        product = product,
                        onClick = { onClick(product) }
                    )
                }
            }
        }
    }
}
