package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import foodyou.app.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MealsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsSettingsViewModel = koinViewModel<MealsSettingsViewModel>()
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val screenState = rememberMealsSettingsScreenState(
        meals = meals,
        onCreate = viewModel::createMeal,
        onUpdate = viewModel::updateMeal,
        onDelete = viewModel::deleteMeal
    )

    MealsSettingsScreen(
        onBack = onBack,
        screenState = screenState,
        formatTime = viewModel::formatTime,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealsSettingsScreen(
    onBack: () -> Unit,
    screenState: MealsSettingsScreenState,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier
) {
    val topBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val createCardFocusRequester = remember { FocusRequester() }
    LaunchedEffect(screenState.creating) {
        if (screenState.creating) {
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
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.headline_meals)
                    )
                },
                scrollBehavior = topBarScrollBehavior
            )
        },
        modifier = modifier.imePadding()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            items(
                items = screenState.meals,
                key = { meal -> meal.id }
            ) { meal ->
                val state = screenState.rememberMealState(meal)

                MealSettingsCard(
                    state = state,
                    showDeleteDialog = true,
                    onDelete = state::onDelete,
                    onConfirm = state::onUpdate,
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
                    screenState = screenState,
                    formatTime = formatTime,
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 16.dp)
                        .focusRequester(createCardFocusRequester)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CreateMealCard(
    screenState: MealsSettingsScreenState,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier
) {
    if (screenState.creating) {
//        MealSettingsCard(
//            state = state,
//            showDeleteDialog = false,
//            onDelete = { screenState.creating = false },
//            onConfirm = {
//
//            },
//            formatTime = formatTime,
//            modifier = modifier
//        )
    } else {
        Card(
            onClick = { screenState.creating = true },
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
                    contentDescription = stringResource(Res.string.action_add_meal)
                )
            }
        }
    }
}
