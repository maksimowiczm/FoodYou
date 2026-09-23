package com.maksimowiczm.foodyou.capabilities.brand

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppNameText(
    brush: Brush,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.brand.displayMedium.copy(brush = brush),
        )
        Text(
            text = "Mateusz Maksimowicz",
            style = MaterialTheme.typography.labelSmall.copy(brush = brush),
        )
    }
}
