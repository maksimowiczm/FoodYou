package com.maksimowiczm.foodyou.app.ui.food.diary.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodDiarySearchFloatingActionButton(
    fabExpanded: Boolean,
    onFabExpandedChange: (Boolean) -> Unit,
    onCreateRecipe: () -> Unit,
    onCreateProduct: () -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    val colorScheme = MaterialTheme.colorScheme

    FloatingActionButtonMenu(
        expanded = fabExpanded,
        modifier = modifier,
        button = {
            ToggleFloatingActionButton(
                checked = fabExpanded,
                onCheckedChange = onFabExpandedChange,
                containerColor = {
                    lerp(
                        start = colorScheme.secondaryContainer,
                        stop = colorScheme.secondary,
                        fraction = it,
                    )
                },
            ) {
                val rotation by remember { derivedStateOf { checkedProgress * 45f } }

                val tintColor =
                    lerp(
                        start = MaterialTheme.colorScheme.onSecondaryContainer,
                        stop = MaterialTheme.colorScheme.onSecondary,
                        fraction = checkedProgress,
                    )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription =
                        if (fabExpanded) {
                            stringResource(Res.string.action_close)
                        } else {
                            stringResource(Res.string.action_create)
                        },
                    tint = tintColor,
                    modifier = Modifier.graphicsLayer { rotationZ = rotation },
                )
            }
        },
    ) {
        FloatingActionButtonMenuItem(
            modifier = Modifier,
            onClick = {
                onCreateRecipe()
                onFabExpandedChange(false)
            },
            icon = { Icon(painterResource(Res.drawable.ic_skillet_filled), null) },
            text = { Text(stringResource(Res.string.headline_recipe)) },
        )
        FloatingActionButtonMenuItem(
            modifier = Modifier,
            onClick = {
                onCreateProduct()
                onFabExpandedChange(false)
            },
            icon = { Icon(Icons.Filled.LunchDining, null) },
            text = { Text(stringResource(Res.string.headline_product)) },
        )
    }
}
