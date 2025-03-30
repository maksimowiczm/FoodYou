package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProductSearchBarSuggestions(
    recentQueries: List<ProductQuery>,
    onSearch: (ProductQuery) -> Unit,
    onFill: (ProductQuery) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(recentQueries) { query ->
            ListItem(
                modifier = Modifier.clickable {
                    onSearch(query)
                },
                headlineContent = {
                    Text(query.query)
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = stringResource(Res.string.action_search)
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = { onFill(query) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NorthWest,
                            contentDescription = stringResource(
                                Res.string.action_insert_suggested_search
                            )
                        )
                    }
                }
            )
        }
    }
}
