package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun DiaryScreen(
    onAddProductToMeal: (Meal, LocalDate) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = koinViewModel()
) {
    val namesOfWeek = remember { viewModel.weekDayNamesShort }
    val referenceDate by viewModel.today.collectAsStateWithLifecycle()
    val zeroDate = remember { viewModel.getFirstDayOfWeek(LocalDate.ofEpochDay(0)) }
    val diaryState = rememberDiaryState(
        zeroDay = zeroDate,
        initialReferenceDate = referenceDate,
        initialSelectedDate = viewModel.initialDate
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            snapshotFlow { diaryState.selectedDate }.collectLatest { date ->
                viewModel.selectDate(date)
            }
        }
    }

    DiaryScreen(
        diaryState = diaryState,
        namesOfDayOfWeek = namesOfWeek,
        getFirstDayOfWeek = viewModel::getFirstDayOfWeek,
        formatMonthYear = viewModel::formatMonthYear,
        formatFullDate = viewModel::formatFullDate,
        observeDiaryDay = viewModel::observeDiaryDay,
        onAddProductToMeal = {
            onAddProductToMeal(it, diaryState.selectedDate)
        },
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}

@Composable
private fun DiaryScreen(
    diaryState: DiaryState,
    namesOfDayOfWeek: Array<String>,
    getFirstDayOfWeek: (LocalDate) -> LocalDate,
    formatMonthYear: (LocalDate) -> String,
    formatFullDate: (LocalDate) -> String,
    observeDiaryDay: (LocalDate) -> Flow<DiaryDay>,
    onAddProductToMeal: (Meal) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = rememberDiaryTopBarScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DiaryTopBar(
                diaryState = diaryState,
                namesOfDayOfWeek = namesOfDayOfWeek,
                getFirstDayOfWeek = getFirstDayOfWeek,
                formatMonthYear = formatMonthYear,
                formatFullDate = formatFullDate,
                scrollBehavior = scrollBehavior,
                colors = DiaryTopBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .displayCutoutPadding()
                .padding(top = 16.dp)
                .padding(horizontal = 8.dp)
        ) {
            item {
                val diaryDay by observeDiaryDay(diaryState.selectedDate).collectAsStateWithLifecycle(
                    null
                )

                if (diaryDay != null) {
                    MealsCard(
                        diaryDay = diaryDay!!,
                        onAddClick = onAddProductToMeal
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun DiaryScreenPreview() {
    val namesOfDayOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val referenceDate = LocalDate.of(2024, 12, 17)
    val selectedDate = LocalDate.of(2024, 12, 18)
    val firstDayOfWeek = { _: LocalDate -> LocalDate.of(2024, 12, 15) }
    val formatMonthYear = { _: LocalDate -> "December 2024" }
    val formatFullDate = { _: LocalDate -> "December 18, 2024" }
    val diaryDay = DiaryDayPreviewParameterProvider().values.first()

    FoodYouTheme {
        SharedTransitionPreview { _, animatedVisibilityScope ->
            DiaryScreen(
                diaryState = rememberDiaryState(
                    zeroDay = LocalDate.ofEpochDay(4),
                    initialReferenceDate = referenceDate,
                    initialSelectedDate = selectedDate
                ),
                namesOfDayOfWeek = namesOfDayOfWeek,
                getFirstDayOfWeek = firstDayOfWeek,
                formatMonthYear = formatMonthYear,
                formatFullDate = formatFullDate,
                observeDiaryDay = { flowOf(diaryDay) },
                onAddProductToMeal = {},
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }
}
