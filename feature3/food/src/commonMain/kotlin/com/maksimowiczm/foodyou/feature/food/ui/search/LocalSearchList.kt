package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LocalSearchList(
    pages: LazyPagingItems<FoodSearch>,
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    onCreateProduct: () -> Unit,
    contentPadding: PaddingValues,
    fabVisible: Boolean,
    modifier: Modifier = Modifier
) = Box(modifier) {
    if (pages.itemCount == 0) {
        Text(
            text = stringResource(Res.string.neutral_no_food_found),
            modifier = Modifier
                .safeContentPadding()
                .align(Alignment.Center)
        )
    }

    if (pages.loadState.refresh is LoadState.Loading ||
        pages.loadState.append is LoadState.Loading
    ) {
        LoadingIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(
                    top = contentPadding.calculateTopPadding()
                )
                .zIndex(10f)
        )
    }

    FloatingActionButton(
        onClick = onCreateProduct,
        modifier = Modifier
            .zIndex(20f)
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
            .animateFloatingActionButton(
                visible = fabVisible,
                alignment = Alignment.BottomEnd
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null
        )
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = contentPadding.add(
            bottom = 56.dp + 32.dp // FAB
        )
    ) {
        items(
            count = pages.itemCount,
            key = pages.itemKey { it.id.toString() }
        ) { i ->
            val food = pages[i]

            if (food != null) {
                val measurement = food.defaultMeasurement

                val topStart = animateDpAsState(
                    targetValue = if (i == 0) 16.dp else 0.dp,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                ).value.coerceAtLeast(0.dp)

                val topEnd = animateDpAsState(
                    targetValue = if (i == 0) 16.dp else 0.dp,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                ).value.coerceAtLeast(0.dp)

                val bottomStart = animateDpAsState(
                    targetValue = if (i == pages.itemCount - 1) {
                        16.dp
                    } else {
                        0.dp
                    },
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                ).value.coerceAtLeast(0.dp)

                val bottomEnd = animateDpAsState(
                    targetValue = if (i == pages.itemCount - 1) {
                        16.dp
                    } else {
                        0.dp
                    },
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                ).value.coerceAtLeast(0.dp)

                val shape = RoundedCornerShape(topStart, topEnd, bottomStart, bottomEnd)

                FoodSearchListItem(
                    food = food,
                    measurement = measurement,
                    onClick = { onFoodClick(food, measurement) },
                    shape = shape
                )
            }

            Spacer(Modifier.height(2.dp))
        }
    }
}
