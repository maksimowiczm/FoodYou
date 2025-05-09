package com.maksimowiczm.foodyou.feature.meal.ui.cardsettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
internal fun MockCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(width = 80.dp, height = 50.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Column(Modifier.padding(8.dp)) {
            Spacer(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.outline)
                    .width(32.dp)
                    .height(8.dp)
            )

            Spacer(Modifier.height(2.dp))

            Spacer(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .width(24.dp)
                    .height(4.dp)
            )

            Spacer(Modifier.weight(1f))

            Row(Modifier.fillMaxWidth()) {
                Spacer(
                    Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .width(40.dp)
                        .height(8.dp)
                )

                Spacer(Modifier.weight(1f))

                Spacer(
                    Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .size(8.dp)
                )
            }
        }
    }
}
