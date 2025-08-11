package com.maksimowiczm.foodyou.feature.food.diary.add.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.feature.food.shared.ui.res.Icon
import com.maksimowiczm.foodyou.feature.food.shared.ui.res.stringResource
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_source
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun Source(source: FoodSource, modifier: Modifier = Modifier.Companion) {
    val url = source.url

    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_source),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.Companion.height(8.dp))

        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            verticalAlignment = Alignment.Companion.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            source.type.Icon()

            Column {
                Text(
                    text = source.type.stringResource(),
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (url != null) {
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
