package com.maksimowiczm.foodyou.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.rememberHomeState
import com.maksimowiczm.foodyou.feature.calendar.CalendarCard
import com.maksimowiczm.foodyou.feature.fooddiary.ui.GoalsCard
import com.maksimowiczm.foodyou.feature.fooddiary.ui.MealsCards
import com.maksimowiczm.foodyou.preferences.HomeCard
import com.maksimowiczm.foodyou.preferences.HomeCardsOrder
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_go_to_settings
import foodyou.app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    goalsCardOnClick: (epochDay: Long) -> Unit,
    goalsCardOnLongClick: () -> Unit,
    mealCardOnAdd: (epochDay: Long, mealId: Long) -> Unit,
    mealCardOnEditMeasurement: (measurementId: Long) -> Unit,
    mealCardOnLongClick: (mealId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val homeState = rememberHomeState()
    val preference = userPreference<HomeCardsOrder>()
    val order by preference.collectAsStateWithLifecycle(preference.getBlocking())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.app_name),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onAbout
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSettings
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
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
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(order) {
                when (it) {
                    HomeCard.Calendar -> CalendarCard(
                        homeState = homeState,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    HomeCard.Goals -> GoalsCard(
                        homeState = homeState,
                        onClick = goalsCardOnClick,
                        onLongClick = goalsCardOnLongClick,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    HomeCard.Meals -> MealsCards(
                        homeState = homeState,
                        onAdd = mealCardOnAdd,
                        onEditMeasurement = mealCardOnEditMeasurement,
                        onLongClick = mealCardOnLongClick,
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
