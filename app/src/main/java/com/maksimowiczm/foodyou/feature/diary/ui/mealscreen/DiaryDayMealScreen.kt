package com.maksimowiczm.foodyou.feature.diary.ui.mealscreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.ui.search.caloriesString
import com.maksimowiczm.foodyou.feature.addfood.ui.search.measurementString
import com.maksimowiczm.foodyou.feature.addfood.ui.search.measurementStringShort
import com.maksimowiczm.foodyou.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlin.math.max
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

@OptIn(ExperimentalMaterial3Api::class)
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
                val fraction = (it - startOffset).coerceAtLeast(0f) / (headlineHeight - startOffset)
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
            FloatingActionButton(
                onClick = {
                    onProductAdd()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
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
            ) { i, it ->
                DiaryDayMealListItem(
                    model = it,
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

@Composable
private fun DiaryDayMealListItem(
    model: ProductWithWeightMeasurement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(
                text = model.product.name
            )
        },
        modifier = modifier
            .horizontalDisplayCutoutPadding()
            .clickable { onClick() },
        overlineContent = {
            model.product.brand?.let {
                Text(
                    text = it
                )
            }
        },
        supportingContent = {
            SupportingTextLayout(
                measurementString = model.measurementString,
                measurementStringShort = model.measurementStringShort,
                caloriesString = model.caloriesString,
                modifier = Modifier.fillMaxWidth()
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun SupportingTextLayout(
    measurementString: String,
    measurementStringShort: String,
    caloriesString: String,
    modifier: Modifier = Modifier
) {
    val textStyle = LocalTextStyle.current
    val measurement = @Composable { Text(text = measurementString, maxLines = 1) }
    val measurementShort = @Composable { Text(text = measurementStringShort, maxLines = 1) }
    val calories = @Composable { Text(text = caloriesString, maxLines = 1) }
    val textMeasurer = rememberTextMeasurer()

    Layout(
        contents = listOf(
            measurement,
            measurementShort,
            calories
        ),
        modifier = modifier
    ) { (measurement, measurementShort, calories), constraints ->
        val measurementWidth = textMeasurer.measure(
            text = measurementString,
            style = textStyle
        ).size.width
        val measurementShortWidth = textMeasurer.measure(
            text = measurementStringShort,
            style = textStyle
        ).size.width
        val caloriesWidth = textMeasurer.measure(
            text = caloriesString,
            style = textStyle
        ).size.width

        if (constraints.maxWidth > measurementWidth + caloriesWidth) {
            val measurementPlaceable =
                measurement.first().measure(Constraints.fixedWidth(measurementWidth))
            val caloriesPlaceable = calories.first().measure(Constraints.fixedWidth(caloriesWidth))

            val height = max(measurementPlaceable.height, caloriesPlaceable.height)

            layout(constraints.maxWidth, height) {
                measurementPlaceable.placeRelative(0, 0)
                caloriesPlaceable.placeRelative(
                    constraints.maxWidth - caloriesPlaceable.width,
                    0
                )
            }
        } else if (constraints.maxWidth > measurementShortWidth + caloriesWidth) {
            val measurementShortPlaceable =
                measurementShort.first().measure(Constraints.fixedWidth(measurementShortWidth))
            val caloriesPlaceable = calories.first().measure(Constraints.fixedWidth(caloriesWidth))

            val height = max(measurementShortPlaceable.height, caloriesPlaceable.height)

            layout(constraints.maxWidth, height) {
                measurementShortPlaceable.placeRelative(0, 0)
                caloriesPlaceable.placeRelative(
                    constraints.maxWidth - caloriesPlaceable.width,
                    0
                )
            }
        } else if (constraints.maxWidth > measurementWidth) {
            val measurementPlaceable =
                measurement.first().measure(Constraints.fixedWidth(measurementWidth))

            val height = measurementPlaceable.height

            layout(constraints.maxWidth, height) {
                measurementPlaceable.placeRelative(0, 0)
            }
        } else {
            val measurementShortPlaceable =
                measurementShort.first().measure(Constraints.fixedWidth(measurementShortWidth))

            val height = measurementShortPlaceable.height

            layout(constraints.maxWidth, height) {
                measurementShortPlaceable.placeRelative(0, 0)
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
