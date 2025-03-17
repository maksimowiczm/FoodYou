package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.data.model.Meal
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_go_back
import foodyou.app.generated.resources.headline_meals
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MealsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsSettingsScreenViewModel = koinViewModel()
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()

    MealsSettingsScreen(
        onBack = onBack,
        meals = meals,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsSettingsScreen(onBack: () -> Unit, meals: List<Meal>, modifier: Modifier = Modifier) {
    val topBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

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
            contentPadding = paddingValues
        ) {
            items(
                items = meals,
                key = { meal -> meal.id }
            ) { meal ->
                MealSettingsCard(
                    viewModel = MealSettingsCardViewModel(
                        diaryRepository = koinInject(),
                        stringFormatRepository = koinInject(),
                        mealId = meal.id,
                        coroutineScope = coroutineScope
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItem()
                )

                Spacer(Modifier.height(8.dp))
            }

            item(
                key = "create"
            ) {
                CreateMealSettingsCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItem()
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
