package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.koin.androidx.compose.koinViewModel

@Composable
fun MealsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsSettingsViewModel = koinViewModel<MealsSettingsViewModel>()
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()

    MealsSettingsScreen(
        onBack = onBack,
        meals = meals,
        onCreate = viewModel::createMeal,
        onUpdate = viewModel::updateMeal,
        onDelete = viewModel::deleteMeal,
        formatTime = viewModel::formatTime,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealsSettingsScreen(
    onBack: () -> Unit,
    meals: List<Meal>,
    onCreate: suspend (name: String, from: LocalTime, to: LocalTime) -> Unit,
    onUpdate: suspend (Meal) -> Unit,
    onDelete: suspend (Meal) -> Unit,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
        .add(WindowInsets.ime)

    val topBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var creating by rememberSaveable { mutableStateOf(false) }
    val createCardFocusRequester = remember { FocusRequester() }
    LaunchedEffect(creating) {
        if (creating) {
            // Focus if possible
            runCatching {
                createCardFocusRequester.requestFocus()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                title = {
                    Text(
                        text = stringResource(R.string.headline_meals)
                    )
                },
                scrollBehavior = topBarScrollBehavior
            )
        },
        contentWindowInsets = contentWindowInsets,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
            }

            items(
                items = meals,
                key = { meal -> meal.id }
            ) { meal ->
                val state = rememberMealsSettingsCardState(meal)

                MealSettingsCard(
                    state = state,
                    showDeleteDialog = true,
                    onDelete = {
                        coroutineScope.launch {
                            state.isLoading = true
                            onDelete(meal)
                        }
                    },
                    onConfirm = {
                        coroutineScope.launch {
                            state.isLoading = true
                            onUpdate(state.intoMeal(meal.id))
                            state.isLoading = false
                        }
                    },
                    formatTime = formatTime,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItem()
                )

                Spacer(Modifier.height(8.dp))
            }

            item(
                key = "create"
            ) {
                CreateMealCard(
                    creating = creating,
                    onCreatingChange = { creating = it },
                    onCreate = onCreate,
                    formatTime = formatTime,
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 16.dp)
                        .focusRequester(createCardFocusRequester),
                    coroutineScope = coroutineScope
                )

                Spacer(Modifier.height(8.dp))
            }

            item(
                key = "bottom"
            ) {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}

@Composable
private fun CreateMealCard(
    creating: Boolean,
    onCreatingChange: (Boolean) -> Unit,
    onCreate: suspend (name: String, from: LocalTime, to: LocalTime) -> Unit,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    if (creating) {
        val state = rememberMealsSettingsCardState()

        MealSettingsCard(
            state = state,
            showDeleteDialog = false,
            onDelete = { onCreatingChange(false) },
            onConfirm = {
                coroutineScope.launch {
                    state.isLoading = true
                    onCreate(state.nameInput.value, state.fromInput.value, state.toInput.value)
                    state.isLoading = false
                    onCreatingChange(false)
                }
            },
            formatTime = formatTime,
            modifier = modifier
        )
    } else {
        Card(
            onClick = { onCreatingChange(true) },
            modifier = modifier,
            colors = CardDefaults.outlinedCardColors(
                containerColor = MealSettingsCardDefaults.colors().containerColor,
                contentColor = MealSettingsCardDefaults.colors().contentColor
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add_meal)
                )
            }
        }
    }
}
