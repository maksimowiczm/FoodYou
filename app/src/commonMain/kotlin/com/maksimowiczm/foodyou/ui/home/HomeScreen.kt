package com.maksimowiczm.foodyou.ui.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.home.rememberHomeState
import com.maksimowiczm.foodyou.feature.calendar.CalendarCard
import com.maksimowiczm.foodyou.feature.meal.MealsCard
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSettings: () -> Unit,
    onMealCardClick: (epochDay: Int, mealId: Long) -> Unit,
    onMealCardAddClick: (epochDay: Int, mealId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val homeState = rememberHomeState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = onSettings
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(Res.string.action_go_to_settings)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                CalendarCard(
                    homeState = homeState,
                    modifier = Modifier.padding(8.dp)
                )
            }

            item {
                MealsCard(
                    animatedVisibilityScope = animatedVisibilityScope,
                    homeState = homeState,
                    onMealClick = onMealCardClick,
                    onAddClick = onMealCardAddClick
                )
            }
        }
    }
}
