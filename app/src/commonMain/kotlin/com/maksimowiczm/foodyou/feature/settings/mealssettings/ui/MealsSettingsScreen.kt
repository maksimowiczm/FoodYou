package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
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
//                CreateMealCard(
//                    creating = creating,
//                    onCreatingChange = { creating = it },
//                    onCreate = screenState::onCreate,
//                    formatTime = formatTime,
//                    modifier = Modifier
//                        .animateItem()
//                        .padding(horizontal = 16.dp)
//                        .focusRequester(createCardFocusRequester),
//                    coroutineScope = coroutineScope
//                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// @Composable
// private fun CreateMealCard(
//    creating: Boolean,
//    onCreatingChange: (Boolean) -> Unit,
//    onCreate: suspend (name: String, from: LocalTime, to: LocalTime) -> Unit,
//    formatTime: (LocalTime) -> String,
//    modifier: Modifier = Modifier,
//    coroutineScope: CoroutineScope = rememberCoroutineScope()
// ) {
//    if (creating) {
//        val state = rememberMealsSettingsCardState()
//
//        MealSettingsCard(
//            state = state,
//            showDeleteDialog = false,
//            onDelete = { onCreatingChange(false) },
//            onConfirm = {
//                coroutineScope.launch {
//                    state.isLoading = true
//                    onCreate(state.nameInput.value, state.fromInput.value, state.toInput.value)
//                    state.isLoading = false
//                    onCreatingChange(false)
//                }
//            },
//            formatTime = formatTime,
//            modifier = modifier
//        )
//    } else {
//        Card(
//            onClick = { onCreatingChange(true) },
//            modifier = modifier,
//            colors = CardDefaults.outlinedCardColors(
//                containerColor = MealSettingsCardDefaults.colors().containerColor,
//                contentColor = MealSettingsCardDefaults.colors().contentColor
//            )
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = stringResource(Res.string.action_add_meal)
//                )
//            }
//        }
//    }
// }
