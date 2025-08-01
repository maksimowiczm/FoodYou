package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun IncompleteFoodsList(
    foods: List<String>,
    onFoodClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val desc = "* " + stringResource(Res.string.description_incomplete_nutrition_data)

    Column(modifier) {
        Text(
            text = desc,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.headline_incomplete_products),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        foods.forEach { food ->
            Text(
                text = food,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = { onFoodClick(food) }
                )
            )
        }
    }
}
