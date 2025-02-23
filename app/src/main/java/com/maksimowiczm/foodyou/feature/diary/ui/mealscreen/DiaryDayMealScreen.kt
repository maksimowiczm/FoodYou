package com.maksimowiczm.foodyou.feature.diary.ui.mealscreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.SharedTransitionKeys
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.ui.ListItem
import com.maksimowiczm.foodyou.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalTime
import org.koin.androidx.compose.koinViewModel

@Composable
fun DiaryDayMealScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onProductAdd: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiaryDayMealViewModel = koinViewModel()
) {
    val diaryDay by viewModel.observeDiaryDay(viewModel.date).collectAsStateWithLifecycle(null)

    if (diaryDay == null) {
    } else {
        val meal = remember(diaryDay) {
            diaryDay!!.meals.first { it.id == viewModel.mealId }
        }

        val products = remember(diaryDay, meal) {
            diaryDay!!.mealProductMap[meal] ?: error("No products for meal ${meal.name}")
        }

        DiaryDayMealScreen(
            animatedVisibilityScope = animatedVisibilityScope,
            meal = meal,
            products = products,
            onProductAdd = onProductAdd,
            onBack = onBack,
            formatTime = viewModel::formatTime,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun DiaryDayMealScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    meal: Meal,
    products: List<ProductWithWeightMeasurement>,
    onProductAdd: () -> Unit,
    onBack: () -> Unit,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier
) {
    val sharedTransitionScope =
        LocalSharedTransitionScope.current ?: error("No shared transition scope")
    val density = LocalDensity.current
    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets

    val topOffsetHeight = 50.dp
    // Fade in top app bar title when scrolling down
    val lazyListState = rememberLazyListState()
    var headlineHeight by remember { mutableIntStateOf(0) }
    var headlineAlpha by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collectLatest {
            headlineAlpha = if (lazyListState.firstVisibleItemIndex != 0) {
                1f
            } else {
                val startOffset = density.run { topOffsetHeight.toPx() }
                val limit = headlineHeight - startOffset
                val fraction = (it - startOffset).coerceAtLeast(0f) / limit
                lerp(0f, 1f, fraction)
            }
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = meal.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer { alpha = headlineAlpha }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            with(sharedTransitionScope) {
                FloatingActionButton(
                    onClick = onProductAdd,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = SharedTransitionKeys.SearchHome
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        enter = crossfadeIn(),
                        exit = crossfadeOut()
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.action_add)
                    )
                }
            }
        },
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .onSizeChanged { headlineHeight = it.height }
                        .graphicsLayer { alpha = 1 - headlineAlpha }
                ) {
                    Spacer(Modifier.height(topOffsetHeight))

                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.outline,
                            LocalTextStyle provides MaterialTheme.typography.bodyLarge
                        ) {
                            Text(
                                text = formatTime(meal.from)
                            )
                            Text(
                                text = stringResource(R.string.en_dash)
                            )
                            Text(
                                text = formatTime(meal.to)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(25.dp))
            }

            itemsIndexed(
                items = products
            ) { i, model ->
                model.ListItem(
                    onClick = {}
                )

                if (i < products.size - 1) {
                    HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                }
            }

            // FAB spacer
            item {
                Spacer(Modifier.height(56.dp + 16.dp + 8.dp))
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DiaryDayMealScreenPreview() {
    val diaryDay = DiaryDayPreviewParameterProvider().values.first()
    val meal = diaryDay.meals.first()

    val products = diaryDay.mealProductMap[meal]!!

    val realProducts = (0..5).map {
        products.random()
    }

    FoodYouTheme {
        SharedTransitionPreview { _, animatedVisibilityScope ->
            DiaryDayMealScreen(
                animatedVisibilityScope = animatedVisibilityScope,
                meal = meal,
                products = realProducts,
                onProductAdd = {},
                onBack = {},
                formatTime = { it.toString() }
            )
        }
    }
}
