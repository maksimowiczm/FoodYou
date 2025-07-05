package com.maksimowiczm.foodyou.feature.food.ui.product.create

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.food.ui.product.ProductForm
import com.maksimowiczm.foodyou.feature.food.ui.product.rememberProductFormState

@Composable
internal fun CreateProductScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentPadding = paddingValues
        ) {
            item {
                ProductForm(
                    state = rememberProductFormState(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}
