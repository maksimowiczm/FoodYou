package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.outlined.Add
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
fun FoodSearchFloatingActionButton(
    fabExpanded: Boolean,
    onFabExpandedChange: (Boolean) -> Unit,
    onCreateRecipe: () -> Unit,
    onCreateProduct: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets =
        WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
            .add(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)),
) {
    val colorScheme = MaterialTheme.colorScheme

    FloatingActionButtonMenu(
        expanded = fabExpanded,
        modifier = modifier.windowInsetsPadding(windowInsets),
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
                    imageVector = Icons.Outlined.Add,
                    contentDescription =
                        if (fabExpanded) stringResource(Res.string.action_close)
                        else stringResource(Res.string.action_create),
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
