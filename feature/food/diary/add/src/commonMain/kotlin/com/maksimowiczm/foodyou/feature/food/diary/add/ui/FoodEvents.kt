package com.maksimowiczm.foodyou.feature.food.diary.add.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.shared.compose.component.unorderedList
import com.maksimowiczm.foodyou.shared.compose.utility.LocalDateFormatter
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodHistory(events: List<FoodHistory>, modifier: Modifier = Modifier.Companion) {
    val dateFormatter = LocalDateFormatter.current
    val strings = events.map { it.stringResource() + ", " + dateFormatter.formatDateTime(it.date) }
    val list = unorderedList(strings)

    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_history),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.Companion.height(8.dp))
        Text(
            text = list,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FoodHistory.stringResource(): String =
    when (this) {
        is FoodHistory.Created -> stringResource(Res.string.headline_created)
        is FoodHistory.Downloaded -> stringResource(Res.string.headline_downloaded)
        is FoodHistory.Imported -> stringResource(Res.string.headline_imported)
        is FoodHistory.Edited -> stringResource(Res.string.headline_edited)
        is FoodHistory.ImportedFromFoodYou2 ->
            stringResource(Res.string.headline_imported_from_food_you_2)
    }
