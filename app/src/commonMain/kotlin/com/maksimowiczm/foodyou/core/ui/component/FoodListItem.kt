package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer

// TODO
//  Update skeleton to match the design
@Composable
fun FoodListItemSkeleton(
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    ListItem(
        headlineContent = {
            Column {
                Spacer(Modifier.height(2.dp))
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp() - 4.dp)
                        .width(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(Modifier.height(2.dp))
            }
        },
        modifier = modifier,
        overlineContent = {
            Spacer(
                Modifier
                    .shimmer(shimmer)
                    .height(LocalTextStyle.current.toDp())
                    .width(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        },
        supportingContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp())
                        .width(125.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                Spacer(
                    Modifier
                        .shimmer(shimmer)
                        .height(LocalTextStyle.current.toDp())
                        .width(75.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        },
        trailingContent = trailingContent
    )
}
