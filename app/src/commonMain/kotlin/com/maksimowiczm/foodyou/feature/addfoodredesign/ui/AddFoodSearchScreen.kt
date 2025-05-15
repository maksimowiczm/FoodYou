package com.maksimowiczm.foodyou.feature.addfoodredesign.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.core.ui.component.BackHandler
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddFoodSearchScreen(
    onProductAdd: () -> Unit,
    onRecipeAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    val scrimAlpha by animateFloatAsState(
        targetValue = if (fabExpanded) .5f else 0f
    )

    BackHandler(fabExpanded) { fabExpanded = false }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Fab(
                expanded = fabExpanded,
                onExpandedChange = { expanded ->
                    fabExpanded = expanded
                },
                onRecipeAdd = {
                    onRecipeAdd()
                    fabExpanded = false
                },
                onProductAdd = {
                    onProductAdd()
                    fabExpanded = false
                }
            )
        }
    ) {
        if (fabExpanded) {
            Spacer(
                Modifier
                    .fillMaxSize()
                    .zIndex(10f)
                    .graphicsLayer { alpha = scrimAlpha }
                    .pointerInput(Unit) { detectTapGestures { fabExpanded = false } }
                    .background(MaterialTheme.colorScheme.scrim)
            )
        }

        Content()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Fab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRecipeAdd: () -> Unit,
    onProductAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = onExpandedChange,
                containerColor = ToggleFloatingActionButtonDefaults.containerColor(
                    initialColor = MaterialTheme.colorScheme.secondaryContainer,
                    finalColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                val rotation by remember {
                    derivedStateOf { checkedProgress * 45f }
                }

                val tintColor = lerp(
                    start = MaterialTheme.colorScheme.onSecondaryContainer,
                    stop = MaterialTheme.colorScheme.onSecondary,
                    fraction = checkedProgress
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (expanded) {
                        stringResource(Res.string.action_close)
                    } else {
                        stringResource(Res.string.action_create)
                    },
                    tint = tintColor,
                    modifier = Modifier.graphicsLayer { rotationZ = rotation }
                )
            }
        },
        modifier = modifier
    ) {
        FloatingActionButtonMenuItem(
            onClick = onRecipeAdd,
            text = { Text(stringResource(Res.string.headline_recipe)) },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_skillet),
                    contentDescription = null
                )
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        FloatingActionButtonMenuItem(
            onClick = onProductAdd,
            icon = {
                Icon(
                    imageVector = Icons.Default.LunchDining,
                    contentDescription = null
                )
            },
            text = { Text(stringResource(Res.string.headline_product)) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun Content(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
        }
    }
}
