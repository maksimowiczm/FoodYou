package com.maksimowiczm.foodyou.feature.meal.ui.cardsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.meal.data.MealCardsLayout
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_horizontal
import foodyou.app.generated.resources.headline_vertical
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LayoutPicker(
    layout: MealCardsLayout,
    onLayoutChange: (MealCardsLayout) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LayoutContainer(
            onLayoutChange = { onLayoutChange(MealCardsLayout.Horizontal) }
        ) {
            LayoutHorizontal()
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.headline_horizontal),
                style = MaterialTheme.typography.labelLarge
            )
            RadioButton(
                selected = layout == MealCardsLayout.Horizontal,
                onClick = { onLayoutChange(MealCardsLayout.Horizontal) }
            )
        }

        LayoutContainer(
            onLayoutChange = { onLayoutChange(MealCardsLayout.Vertical) }
        ) {
            LayoutVertical()
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.headline_vertical),
                style = MaterialTheme.typography.labelLarge
            )
            RadioButton(
                selected = layout == MealCardsLayout.Vertical,
                onClick = { onLayoutChange(MealCardsLayout.Vertical) }
            )
        }
    }
}

@Composable
private fun LayoutContainer(
    onLayoutChange: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onLayoutChange() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}
