package com.maksimowiczm.foodyou.feature.food.diary.search.ui

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.DiaryFoodSearchViewModel
import com.maksimowiczm.foodyou.feature.food.shared.ui.search.FoodSearchApp
import com.maksimowiczm.foodyou.feature.food.shared.ui.search.FoodSearchAppDefaults
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.BackHandler
import com.maksimowiczm.foodyou.shared.ui.Scrim
import com.maksimowiczm.foodyou.shared.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.ext.toDp
import com.maksimowiczm.foodyou.shared.ui.utils.LocalDateFormatter
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.neutral_measurement_added
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun DiaryFoodSearchScreen(
    onBack: () -> Unit,
    onCreateRecipe: () -> Unit,
    onCreateProduct: () -> Unit,
    onMeasure: (FoodId, Measurement) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    date: LocalDate,
    mealId: Long,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = LocalDateFormatter.current

    val viewModel: DiaryFoodSearchViewModel = koinViewModel { parametersOf(mealId) }
    val meal = viewModel.meal.collectAsStateWithLifecycle().value

    val snackBarHostState = remember { SnackbarHostState() }
    val message = stringResource(Res.string.neutral_measurement_added)

    LaunchedCollectWithLifecycle(viewModel.newEntryEvents) {
        snackBarHostState.showSnackbar(message)
    }

    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(fabExpanded) { fabExpanded = false }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val topBar =
        @Composable {
            TopAppBar(
                title = {
                    updateTransition(meal).Crossfade(contentKey = { it?.toString() }) {
                        if (meal == null) {
                            Spacer(
                                modifier =
                                    Modifier.height(LocalTextStyle.current.toDp() - 4.dp)
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
                subtitle = { Text(dateFormatter.formatDate(date)) },
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        }
    val content: @Composable (PaddingValues) -> Unit =
        @Composable { paddingValues ->
            FoodSearchApp(
                onFoodClick = { model, measurement -> onMeasure(model.id, measurement) },
                onUpdateUsdaApiKey = onUpdateUsdaApiKey,
                modifier =
                    Modifier.padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
            )
        }

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
            topBar = topBar,
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top),
            floatingActionButton = {
                Box(modifier = Modifier.windowInsetsPadding(fabInsets).height(56.dp))
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
            content = content,
        )
    }
}
