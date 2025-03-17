package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

    var isCreating by rememberSaveable { mutableStateOf(false) }

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
        modifier = modifier
            .imePadding()
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom))
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .animateContentSize()
        ) {
            meals.forEachIndexed { i, meal ->
                key(meal.id) {
                    MealSettingsCard(
                        viewModel = MealSettingsCardViewModel(
                            diaryRepository = koinInject(),
                            stringFormatRepository = koinInject(),
                            mealId = meal.id,
                            coroutineScope = coroutineScope
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }

            key("create") {
                CreateMealSettingsCard(
                    isCreating = isCreating,
                    onCreatingChange = { isCreating = it },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    coroutineScope = coroutineScope
                )
            }

            Spacer(Modifier.height(8.dp))

            val bottomPadding = ScaffoldDefaults.contentWindowInsets
                .only(WindowInsetsSides.Bottom)
                .asPaddingValues().calculateBottomPadding()
            Spacer(Modifier.height(bottomPadding))
        }
    }
}
