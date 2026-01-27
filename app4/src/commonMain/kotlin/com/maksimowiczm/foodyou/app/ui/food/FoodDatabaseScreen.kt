package com.maksimowiczm.foodyou.app.ui.food

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.app.ui.common.component.Scrim
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtection
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtectionDefaults
import com.maksimowiczm.foodyou.app.ui.food.search.FoodIdentity
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchApp
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FoodDatabaseScreen(
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onFood: (FoodProductIdentity) -> Unit,
    onUserFood: (UserFoodProductIdentity) -> Unit,
    query: String?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = fabExpanded,
        onBackCompleted = { fabExpanded = true },
    )

    val offset = rememberSaveable { mutableFloatStateOf(0f) }
    val scrollConnection = StatusBarProtectionDefaults.scrollConnection { offset.value -= it.y }
    val topBarHeight = LocalDensity.current.run { (56 * 3).dp.toPx() }

    Box(modifier) {
        //        val fabInsets =
        //            WindowInsets.systemBars
        //                .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
        //                .add(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
        //        FoodSearchAppDefaults.FloatingActionButton(
        //            fabExpanded = fabExpanded,
        //            onFabExpandedChange = { fabExpanded = it },
        //            onCreateRecipe = onCreateRecipe,
        //            onCreateProduct = onCreateProduct,
        //            modifier =
        //                Modifier.zIndex(100f)
        //                    .align(Alignment.BottomEnd)
        //                    .windowInsetsPadding(fabInsets)
        //                    .consumeWindowInsets(fabInsets)
        //                    .animateFloatingActionButton(
        //                        visible = !animatedVisibilityScope.transition.isRunning,
        //                        alignment = Alignment.BottomEnd,
        //                    ),
        //        )
        Scrim(
            visible = fabExpanded,
            onDismiss = { fabExpanded = false },
            modifier = Modifier.fillMaxSize().zIndex(10f),
        )
        Scaffold(
            floatingActionButton = {
                // Box(modifier = Modifier.windowInsetsPadding(fabInsets).height(56.dp))
                FloatingActionButton(
                    onClick = onCreateProduct,
                    modifier =
                        Modifier.animateFloatingActionButton(
                            visible = !animatedVisibilityScope.transition.isRunning,
                            alignment = Alignment.BottomEnd,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(Res.string.action_add),
                        modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize),
                    )
                }
            },
            modifier = Modifier.fillMaxSize().nestedScroll(scrollConnection),
            content = {
                FoodSearchApp(
                    onFoodClick = { model ->
                        when (val identity = model.identity) {
                            is FoodIdentity.Other -> onFood(identity.identity)
                            is FoodIdentity.UserFood -> onUserFood(identity.identity)
                        }
                    },
                    onBack = onBack,
                    query = query,
                )
            },
        )
        StatusBarProtection(MaterialTheme.colorScheme.surfaceContainerHigh) {
            (offset.value / topBarHeight).coerceIn(0f, 1f)
        }
    }
}
