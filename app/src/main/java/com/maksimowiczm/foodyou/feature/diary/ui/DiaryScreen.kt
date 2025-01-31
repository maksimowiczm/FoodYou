package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.ui.nutriments.NutrimentsRowCard
import com.maksimowiczm.foodyou.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun DiaryScreen(
    onAddProductToMeal: (Meal, LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = koinViewModel()
) {
    val namesOfWeek = remember { viewModel.weekDayNamesShort }
    val referenceDate by viewModel.today.collectAsStateWithLifecycle()
    val zeroDate = remember { LocalDate.ofEpochDay(0) }
    val diaryState = rememberDiaryState(
        namesOfDayOfWeek = namesOfWeek,
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
        formatMonthYear = viewModel::formatMonthYear,
        observeDiaryDay = viewModel::observeDiaryDay,
        onAddProductToMeal = {
            onAddProductToMeal(it, diaryState.selectedDate)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryScreen(
    diaryState: DiaryState,
    formatMonthYear: (LocalDate) -> String,
    observeDiaryDay: (LocalDate) -> Flow<DiaryDay>,
    onAddProductToMeal: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.headline_diary)
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .displayCutoutPadding()
        ) {
            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                CalendarCard(
                    diaryState = diaryState,
                    formatMonthYear = formatMonthYear,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                val diaryDay by observeDiaryDay(diaryState.selectedDate).collectAsStateWithLifecycle(
                    null
                )

                if (diaryDay != null) {
                    MealsCard(
                        diaryDay = diaryDay!!,
                        onAddClick = onAddProductToMeal,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                val diaryDay by observeDiaryDay(diaryState.selectedDate).collectAsStateWithLifecycle(
                    null
                )

                if (diaryDay != null) {
                    NutrimentsRowCard(
                        diaryDay = diaryDay!!
                    )
                }
            }

            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DiaryScreenPreview() {
    val namesOfDayOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val referenceDate = LocalDate.of(2024, 12, 17)
    val selectedDate = LocalDate.of(2024, 12, 18)
    val formatMonthYear = { _: LocalDate -> "December 2024" }
    val diaryDay = DiaryDayPreviewParameterProvider().values.first()

    FoodYouTheme {
        DiaryScreen(
            diaryState = rememberDiaryState(
                namesOfDayOfWeek = namesOfDayOfWeek,
                zeroDay = LocalDate.ofEpochDay(4),
                initialReferenceDate = referenceDate,
                initialSelectedDate = selectedDate
            ),
            formatMonthYear = formatMonthYear,
            observeDiaryDay = { flowOf(diaryDay) },
            onAddProductToMeal = {}
        )
    }
}
