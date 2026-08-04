package com.maksimowiczm.foodyou.app.ui.food.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton

@Composable
internal fun FoodScreenTopBar(
    onBack: () -> Unit,
    title: String?,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable RowScope.() -> Unit,
) {
    val titleAlpha by
        remember(scrollBehavior) { derivedStateOf { scrollBehavior.state.overlappedFraction } }

    TopAppBar(
        title = {
            if (title != null) {
                Text(
                    text = title,
                    modifier = Modifier.graphicsLayer { alpha = titleAlpha },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = { ArrowBackIconButton(onBack) },
        actions = actions,
        colors =
            TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
        scrollBehavior = scrollBehavior,
    )
}
