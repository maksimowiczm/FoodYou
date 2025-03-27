package com.maksimowiczm.foodyou.feature.search.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.feature.search.domain.Product
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(modifier: Modifier = Modifier, viewModel: SearchViewModel = koinViewModel()) {
    val pages = viewModel.pages.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )

    SearchScreen(
        pages = pages,
        modifier = modifier
    )
}

@Composable
private fun SearchScreen(pages: LazyPagingItems<Product>, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
            items(
                count = pages.itemCount,
                key = pages.itemKey { it.id }
            ) {
                val product = pages[it] ?: return@items

                Text(text = product.name)
            }
        }
    }
}
