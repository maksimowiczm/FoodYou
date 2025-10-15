package com.maksimowiczm.foodyou.app.ui.food

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.app.ui.common.component.Scrim
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtection
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtectionDefaults
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchApp
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchAppDefaults

@Composable
fun FoodDatabaseScreen(
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(fabExpanded) { fabExpanded = false }

    val offset = rememberSaveable { mutableFloatStateOf(0f) }
    val scrollConnection = StatusBarProtectionDefaults.scrollConnection { offset.value -= it.y }
    val topBarHeight = LocalDensity.current.run { (56 * 3).dp.toPx() }

    Box(modifier) {
        val fabInsets =
            WindowInsets.systemBars
                .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                .add(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
        FoodSearchAppDefaults.FloatingActionButton(
            fabExpanded = fabExpanded,
            onFabExpandedChange = { fabExpanded = it },
            onCreateRecipe = onCreateRecipe,
            onCreateProduct = onCreateProduct,
            modifier =
                Modifier.zIndex(100f)
                    .align(Alignment.BottomEnd)
                    .windowInsetsPadding(fabInsets)
                    .consumeWindowInsets(fabInsets)
                    .animateFloatingActionButton(
                        visible = !animatedVisibilityScope.transition.isRunning,
                        alignment = Alignment.BottomEnd,
                    ),
        )
        Scrim(
            visible = fabExpanded,
            onDismiss = { fabExpanded = false },
            modifier = Modifier.fillMaxSize().zIndex(10f),
        )
        Scaffold(
            floatingActionButton = {
                Box(modifier = Modifier.windowInsetsPadding(fabInsets).height(56.dp))
            },
            modifier = Modifier.fillMaxSize().nestedScroll(scrollConnection),
            content = {
                FoodSearchApp(
                    onFoodClick = { model, quantity ->
                        // TODO
                    },
                    onBack = onBack,
                    onUpdateUsdaApiKey = onUpdateUsdaApiKey,
                )
            },
        )
    }
    StatusBarProtection { (offset.value / topBarHeight).coerceIn(0f, 1f) }
}
