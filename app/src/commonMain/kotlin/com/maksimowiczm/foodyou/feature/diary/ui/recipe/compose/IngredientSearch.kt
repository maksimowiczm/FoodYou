package com.maksimowiczm.foodyou.feature.diary.ui.recipe.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.CreateRecipeViewModel

@Composable
fun IngredientSearch(viewModel: CreateRecipeViewModel, modifier: Modifier = Modifier) {
    val pages = viewModel.pages.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
            items(
                count = pages.itemCount
            ) {
                val item = pages[it]
                if (item != null) {
                    Text(
                        text = item.toString()
                    )
                }
            }
        }
    }
}
