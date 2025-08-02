package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Meal
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun GoalsScreen(
    onBack: () -> Unit,
    viewModel: GoalsViewModel,
    modifier: Modifier = Modifier
) {
    val date by viewModel.date.collectAsStateWithLifecycle()
    val meals = viewModel.meals.collectAsStateWithLifecycle().value

    if (meals == null) {
        // TODO loading state
    } else {
        GoalsScreen(
            onBack = onBack,
            date = date,
            meals = meals,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GoalsScreen(
    onBack: () -> Unit,
    date: LocalDate,
    meals: List<Meal>,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalDateFormatter.current

    var selectedMealsIds by rememberSaveable(meals) {
        mutableStateOf(meals.map { it.id })
    }

    val filteredMeals = remember(meals, selectedMealsIds) {
        meals.filter { it.id in selectedMealsIds }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_summary)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                subtitle = { Text(dateFormatter.formatDate(date)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                MealsFilter(
                    meals = meals,
                    selectedMealsIds = selectedMealsIds,
                    onSelectedMealsIdsChange = { selectedMealsIds = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            item {
            }
        }
    }
}

@Composable
private fun MealsFilter(
    meals: List<Meal>,
    selectedMealsIds: List<Long>,
    onSelectedMealsIdsChange: (List<Long>) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        meals.forEachIndexed { i, meal ->
            val selected = meal.id in selectedMealsIds

            key(meal.id) {
                FilterChip(
                    selected = selected,
                    onClick = {
                        val selectedMealsIds = if (selected) {
                            selectedMealsIds - meal.id
                        } else {
                            selectedMealsIds + meal.id
                        }
                        onSelectedMealsIdsChange(selectedMealsIds)
                    },
                    label = { Text(meal.name) },
                    modifier = Modifier.animatePlacement(),
                    leadingIcon = {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    }
                )
            }
        }
    }
}
