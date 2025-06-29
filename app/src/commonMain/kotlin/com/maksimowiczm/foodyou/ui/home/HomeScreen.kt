package com.maksimowiczm.foodyou.ui.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.home.rememberHomeState
import com.maksimowiczm.foodyou.feature.calendar.CalendarCard
import com.maksimowiczm.foodyou.feature.goals.GoalsCard
import com.maksimowiczm.foodyou.feature.meal.MealsCards
import com.maksimowiczm.foodyou.preferences.HomeCard
import com.maksimowiczm.foodyou.preferences.HomeOrder
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onEditMeasurement: (measurementId: Long) -> Unit,
    onMealCardLongClick: () -> Unit,
    onMealCardAddClick: (epochDay: Long, mealId: Long) -> Unit,
    onGoalsCardClick: (epochDay: Long) -> Unit,
    onGoalsCardLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    homeOrder: HomeOrder = userPreference()
) {
    val order by homeOrder.collectAsStateWithLifecycle(homeOrder.getBlocking())

    HomeScreen(
        order = order,
        onSettings = onSettings,
        onAbout = onAbout,
        onEditMeasurement = onEditMeasurement,
        onMealCardLongClick = onMealCardLongClick,
        onMealCardAddClick = onMealCardAddClick,
        onGoalsCardClick = onGoalsCardClick,
        onGoalsCardLongClick = onGoalsCardLongClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    order: List<HomeCard>,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onEditMeasurement: (measurementId: Long) -> Unit,
    onMealCardLongClick: () -> Unit,
    onMealCardAddClick: (epochDay: Long, mealId: Long) -> Unit,
    onGoalsCardClick: (epochDay: Long) -> Unit,
    onGoalsCardLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val homeState = rememberHomeState()

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
            modifier = Modifier
                .testTag(HomeScreenTestTags.CARDS_LIST)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(PaddingValues(bottom = 8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = order,
                key = { it.name }
            ) {
                val testTag = HomeScreenTestTags.Card(it).toString()

                when (it) {
                    HomeCard.Calendar -> CalendarCard(
                        homeState = homeState,
                        modifier = Modifier
                            .testTag(testTag)
                            .padding(horizontal = 8.dp)
                    )

                    HomeCard.Meals -> MealsCards(
                        homeState = homeState,
                        onAdd = onMealCardAddClick,
                        onLongClick = onMealCardLongClick,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        onEditMeasurement = onEditMeasurement,
                        modifier = Modifier.testTag(testTag)
                    )

                    HomeCard.Goals -> GoalsCard(
                        homeState = homeState,
                        onClick = {
                            onGoalsCardClick(homeState.selectedDate.toEpochDays())
                        },
                        onLongClick = onGoalsCardLongClick,
                        modifier = Modifier
                            .testTag(testTag)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
