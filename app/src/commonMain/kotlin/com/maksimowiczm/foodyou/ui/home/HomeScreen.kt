package com.maksimowiczm.foodyou.ui.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ui.home.rememberHomeState
import com.maksimowiczm.foodyou.data.HomePreferences
import com.maksimowiczm.foodyou.feature.calendar.CalendarCard
import com.maksimowiczm.foodyou.feature.changelog.AppUpdateChangelogModalBottomSheet
import com.maksimowiczm.foodyou.feature.goals.CaloriesCard
import com.maksimowiczm.foodyou.feature.meal.MealsCard
import com.maksimowiczm.foodyou.ui.settings.home.HomeCard
import com.maksimowiczm.foodyou.ui.settings.home.toHomeCards
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onMealCardClick: (epochDay: Int, mealId: Long) -> Unit,
    onMealCardAddClick: (epochDay: Int, mealId: Long) -> Unit,
    onCaloriesCardClick: (epochDay: Int) -> Unit,
    modifier: Modifier = Modifier,
    dataStore: DataStore<Preferences> = koinInject()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val homeState = rememberHomeState()

    val order by dataStore
        .observe(HomePreferences.homeOrder)
        .map { it.toHomeCards() }
        .collectAsStateWithLifecycle(dataStore.getBlocking(HomePreferences.homeOrder).toHomeCards())

    AppUpdateChangelogModalBottomSheet()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.app_name),
                        modifier = Modifier.clickable(
                            onClick = onAbout,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    )
                },
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
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = order
            ) {
                when (it) {
                    HomeCard.Calendar -> CalendarCard(
                        homeState = homeState,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    HomeCard.Meals -> MealsCard(
                        animatedVisibilityScope = animatedVisibilityScope,
                        homeState = homeState,
                        onMealClick = onMealCardClick,
                        onAddClick = onMealCardAddClick,
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    )

                    HomeCard.Calories -> CaloriesCard(
                        animatedVisibilityScope = animatedVisibilityScope,
                        homeState = homeState,
                        onClick = onCaloriesCardClick,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
