package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.unit_gram_short
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutrientsRow(proteins: Int, carbohydrates: Int, fats: Int, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelMedium
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = proteins.toString() + " " + stringResource(Res.string.unit_gram_short),
                color = nutrientsPalette.proteinsOnSurfaceContainer
            )

            Text(
                text = carbohydrates.toString() + " " + stringResource(Res.string.unit_gram_short),
                color = nutrientsPalette.carbohydratesOnSurfaceContainer
            )

            Text(
                text = fats.toString() + " " + stringResource(Res.string.unit_gram_short),
                color = nutrientsPalette.fatsOnSurfaceContainer
            )
        }
    }
}
