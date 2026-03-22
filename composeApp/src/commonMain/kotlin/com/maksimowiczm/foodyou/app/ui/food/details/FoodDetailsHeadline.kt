package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.valentinilk.shimmer.shimmer

@Composable
internal fun FoodDetailsHeadline(headline: String?, modifier: Modifier = Modifier.Companion) {
    Box(modifier.fillMaxWidth().padding(16.dp)) {
        if (headline != null) {
            Text(text = headline, style = MaterialTheme.typography.displaySmall)
        } else {
            Spacer(
                Modifier.shimmer()
                    .fillMaxWidth(.75f)
                    .height(MaterialTheme.typography.displaySmall.toDp())
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    }
}
