package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.ext.plus
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductForm(contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding + PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Text(
                text = stringResource(Res.string.headline_general),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(stringResource(Res.string.product_name)) },
                supportingText = {
                    Text("* " + stringResource(Res.string.neutral_required))
                }
            )
        }

        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(stringResource(Res.string.product_brand)) }
            )
        }

        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(stringResource(Res.string.product_barcode)) }
            )
        }
    }
}
