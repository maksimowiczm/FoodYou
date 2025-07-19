package com.maksimowiczm.foodyou.feature.fooddiary.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.BackHandler
import com.maksimowiczm.foodyou.core.ui.Scrim
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.FoodSearchApp
import com.maksimowiczm.foodyou.feature.food.ui.FoodSearchAppDefaults
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.valentinilk.shimmer.shimmer
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun FoodSearchScreen(
    onBack: () -> Unit,
    onFoodClick: (foodId: FoodId, measurement: Measurement) -> Unit,
    onCreateProduct: () -> Unit,
    onCreateRecipe: () -> Unit,
    mealId: Long,
    date: LocalDate,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val database: FoodDiaryDatabase = koinInject()
    val meal = database.mealDao.observeMealById(mealId).collectAsStateWithLifecycle(null).value

    val dateFormatter = LocalDateFormatter.current

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(fabExpanded) { fabExpanded = false }

    val topBar = @Composable {
        TopAppBar(
            title = {
                updateTransition(meal).Crossfade(
                    contentKey = { it?.toString() }
                ) {
                    if (meal == null) {
                        Spacer(
                            modifier = Modifier
                                .height(LocalTextStyle.current.toDp() - 4.dp)
                                .width(100.dp)
                                .padding(bottom = 4.dp)
                                .shimmer()
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                        )
                    } else {
                        Text(meal.name)
                    }
                }
            },
            subtitle = {
                Text(dateFormatter.formatDate(date))
            },
            titleHorizontalAlignment = Alignment.CenterHorizontally,
            navigationIcon = { ArrowBackIconButton(onBack) },
            scrollBehavior = scrollBehavior
        )
    }
    val content: @Composable (PaddingValues) -> Unit = @Composable { paddingValues ->
        FoodSearchApp(
            onFoodClick = { food, measurement ->
                onFoodClick(food.id, measurement)
            },
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }

    Box(modifier) {
        val fabInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
            .add(WindowInsets.displayCutout)

        FoodSearchAppDefaults.FloatingActionButton(
            fabExpanded = fabExpanded,
            onFabExpandedChange = { fabExpanded = it },
            onCreateRecipe = onCreateRecipe,
            onCreateProduct = onCreateProduct,
            modifier = Modifier
                .zIndex(100f)
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(fabInsets)
                .consumeWindowInsets(fabInsets)
                .animateFloatingActionButton(
                    visible = !animatedVisibilityScope.transition.isRunning,
                    alignment = Alignment.BottomEnd
                )
        )
        Scrim(
            visible = fabExpanded,
            onDismiss = { fabExpanded = false },
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f)
        )
        Scaffold(
            topBar = topBar,
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
                WindowInsetsSides.Top
            ),
            content = content
        )
    }
}
