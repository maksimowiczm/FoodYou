package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_experimental
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExperimentalBadge(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.tertiary,
    contentColor: Color = MaterialTheme.colorScheme.onTertiary
) {
    Badge(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        Text(stringResource(Res.string.headline_experimental))
    }
}
