package com.maksimowiczm.foodyou.feature.food.diary.add.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.shared.ui.unorderedList
import com.maksimowiczm.foodyou.shared.ui.utils.LocalDateFormatter
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_created
import foodyou.app.generated.resources.headline_downloaded
import foodyou.app.generated.resources.headline_edited
import foodyou.app.generated.resources.headline_history
import foodyou.app.generated.resources.headline_imported
import foodyou.app.generated.resources.headline_imported_from_food_you_2
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodEvents(events: List<FoodEvent>, modifier: Modifier = Modifier.Companion) {
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
private fun FoodEvent.stringResource(): String =
    when (this) {
        is FoodEvent.Created -> stringResource(Res.string.headline_created)
        is FoodEvent.Downloaded -> stringResource(Res.string.headline_downloaded)
        is FoodEvent.Imported -> stringResource(Res.string.headline_imported)
        is FoodEvent.Edited -> stringResource(Res.string.headline_edited)
        is FoodEvent.ImportedFromFoodYou2 ->
            stringResource(Res.string.headline_imported_from_food_you_2)
    }
