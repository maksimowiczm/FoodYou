package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import java.time.LocalDate

@Composable
fun DiaryTopBar(
    diaryState: DiaryState,
    namesOfDayOfWeek: Array<String>,
    getFirstDayOfWeek: (LocalDate) -> LocalDate,
    formatMonthYear: (LocalDate) -> String,
    formatFullDate: (LocalDate) -> String,
    scrollBehavior: DiaryTopBarScrollBehavior,
    modifier: Modifier = Modifier,
    colors: DiaryTopBarColors = DiaryTopBarDefaults.colors(),
    shape: Shape = DiaryTopBarDefaults.shape
) {
    val insets = WindowInsets.systemBars.only(
        WindowInsetsSides.Horizontal + WindowInsetsSides.Top
    )

    val displayCutoutHorizontal = WindowInsets.displayCutout.only(
        WindowInsetsSides.Horizontal
    )

    Surface(
        modifier = modifier
            .windowInsetsPadding(displayCutoutHorizontal)
            .consumeWindowInsets(displayCutoutHorizontal)
            .clip(shape),
        color = colors.containerColor
    ) {
        DiaryTopBarInt(
            diaryState = diaryState,
            namesOfDayOfWeek = namesOfDayOfWeek,
            getFirstDayOfWeek = getFirstDayOfWeek,
            formatMonthYear = formatMonthYear,
            formatFullDate = formatFullDate,
            scrollBehavior = scrollBehavior,
            colors = colors,
            modifier = Modifier
                .windowInsetsPadding(insets)
                .consumeWindowInsets(insets)
        )
    }
}

@Composable
private fun DiaryTopBarInt(
    diaryState: DiaryState,
    namesOfDayOfWeek: Array<String>,
    getFirstDayOfWeek: (LocalDate) -> LocalDate,
    formatMonthYear: (LocalDate) -> String,
    formatFullDate: (LocalDate) -> String,
    scrollBehavior: DiaryTopBarScrollBehavior,
    colors: DiaryTopBarColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .anchoredDraggable(
                state = scrollBehavior.anchoredDraggableState,
                orientation = Orientation.Vertical
            )
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(scrollBehavior.headlineHeight)
                .clipToBounds()
                .padding(horizontal = DiaryTopBarDefaults.headlineHorizontalPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(DiaryTopBarDefaults.headlineHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.headline_diary),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    color = colors.textColor
                )
            }
        }

        DiaryDatePicker(
            diaryState = diaryState,
            namesOfDayOfWeek = namesOfDayOfWeek,
            getFirstDayOfWeek = getFirstDayOfWeek,
            formatMonthYear = formatMonthYear,
            formatFullDate = formatFullDate,
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    }
}

@Composable
private fun ColumnScope.DiaryDatePicker(
    diaryState: DiaryState,
    namesOfDayOfWeek: Array<String>,
    getFirstDayOfWeek: (LocalDate) -> LocalDate,
    formatMonthYear: (LocalDate) -> String,
    formatFullDate: (LocalDate) -> String,
    colors: DiaryTopBarColors,
    scrollBehavior: DiaryTopBarScrollBehavior
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(DiaryTopBarDefaults.dateStringHeight)
            .padding(horizontal = DiaryTopBarDefaults.dateStringHorizontalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = scrollBehavior.state == DiaryTopBarScrollBehavior.SizeState.Collapsed
        ) {
            if (it) {
                Text(
                    text = formatFullDate(diaryState.selectedDate),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textColor
                )
            } else {
                Text(
                    text = formatMonthYear(getFirstDayOfWeek(diaryState.targetWeek)),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textColor
                )
            }
        }
        AnimatedVisibility(
            visible = diaryState.referenceDateSelected ||
                diaryState.referenceWeekPage != diaryState.weekPagerState.currentPage,
            enter = slideInHorizontally(initialOffsetX = { 2 * it }),
            exit = slideOutHorizontally(targetOffsetX = { 2 * it })
        ) {
            // TODO
            //  Sometimes button won't fit.
            TextButton(
                onClick = { diaryState.onDateSelect(diaryState.referenceDate) }
            ) {
                Text(
                    text = stringResource(R.string.action_go_to_today),
                    maxLines = 1
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = scrollBehavior.horizontalPagerMaxHeight)
            .padding(horizontal = DiaryTopBarDefaults.datePickerHorizontalPadding)
            .clipToBounds()
    ) {
        DiaryDatePickerHorizontalPager(
            diaryState = diaryState,
            namesOfDayOfWeek = namesOfDayOfWeek,
            colors = colors,
            scrollBehavior = scrollBehavior,
            modifier = Modifier.requiredHeightIn(
                min = DiaryTopBarDefaults.datePickerMinHeight,
                max = DiaryTopBarDefaults.datePickerMaxHeight
            )
        )
    }
}

@Composable
fun DiaryDatePickerHorizontalPager(
    diaryState: DiaryState,
    namesOfDayOfWeek: Array<String>,
    colors: DiaryTopBarColors,
    scrollBehavior: DiaryTopBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    // Measure only once per configuration change
    var measured = remember { false }

    Surface(
        modifier = modifier.onSizeChanged {
            if (!measured) {
                measured = true
                scrollBehavior.setDatePickerHeight(it.height)
            }
        },
        color = colors.containerColor
    ) {
        HorizontalPager(
            // Somehow user can scroll horizontally even if the top bar is collapsed and pager is
            // now visible. No idea if this is a material ui bug or just mess with layouts but this
            // is a workaround.
            userScrollEnabled = scrollBehavior.anchoredDraggableState.currentValue !=
                DiaryTopBarScrollBehavior.SizeState.Collapsed,
            state = diaryState.weekPagerState
        ) { page ->
            val start = diaryState.zeroDate.plusWeeks(page.toLong())

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DiaryDatePickerRow(
                    start = start,
                    namesOfDayOfWeek = namesOfDayOfWeek,
                    selectedDate = diaryState.selectedDate,
                    onDateSelect = diaryState::onDateSelect,
                    referenceDate = diaryState.referenceDate,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun DiaryDatePickerRow(
    start: LocalDate,
    namesOfDayOfWeek: Array<String>,
    referenceDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelect: (LocalDate) -> Unit,
    colors: DiaryTopBarColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 7) {
            val date = start.plusDays(i.toLong())

            val backgroundColor by animateColorAsState(
                targetValue = when (date) {
                    selectedDate -> colors.selectedDateContainerColor
                    referenceDate -> colors.referenceDateContainerColor
                    else -> colors.containerColor
                },
                animationSpec = tween(500),
                label = "Date background color"
            )
            val color by animateColorAsState(
                targetValue = when (date) {
                    selectedDate -> colors.selectedDateTextColor
                    referenceDate -> colors.referenceDateTextColor
                    else -> colors.textColor
                },
                animationSpec = tween(500),
                label = "Date text color"
            )

            // Squares. If they don't fit then grow height.
            Column(
                modifier = Modifier
                    .weight(1f, false)
                    .sizeIn(
                        minHeight = DiaryTopBarDefaults.datePickerMinHeight,
                        minWidth = DiaryTopBarDefaults.datePickerMinHeight,
                        maxHeight = DiaryTopBarDefaults.datePickerMaxHeight,
                        maxWidth = DiaryTopBarDefaults.datePickerMaxHeight
                    )
                    .clip(MaterialTheme.shapes.medium)
                    .drawBehind { drawRect(backgroundColor) }
                    .clickable(onClick = { onDateSelect(date) }),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = namesOfDayOfWeek[date.dayOfWeek.value - 1],
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Clip,
                    maxLines = 1,
                    color = color
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    color = color
                )
            }
        }
    }
}

data class DiaryTopBarColors(
    val containerColor: Color,
    val textColor: Color,
    val selectedDateContainerColor: Color,
    val selectedDateTextColor: Color,
    val referenceDateContainerColor: Color,
    val referenceDateTextColor: Color
)

private val defaultDiaryTopBarColors
    @Composable get() = DiaryTopBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        textColor = MaterialTheme.colorScheme.onSurface,
        selectedDateContainerColor = MaterialTheme.colorScheme.primary,
        selectedDateTextColor = MaterialTheme.colorScheme.onPrimary,
        referenceDateContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        referenceDateTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

object DiaryTopBarDefaults {
    val headlineHeight = 64.dp
    val headlineHorizontalPadding = 16.dp

    val dateStringHeight = 48.dp
    val dateStringHorizontalPadding = 16.dp

    val datePickerMinHeight = 56.dp
    val datePickerMaxHeight = 90.dp
    val datePickerHorizontalPadding = 16.dp

    val maxHeight = headlineHeight + dateStringHeight + datePickerMaxHeight
    val minHeight = headlineHeight + dateStringHeight + datePickerMinHeight

    @Composable
    fun colors(
        containerColor: Color = defaultDiaryTopBarColors.containerColor,
        textColor: Color = defaultDiaryTopBarColors.textColor,
        selectedDateContainerColor: Color = defaultDiaryTopBarColors.selectedDateContainerColor,
        selectedDateTextColor: Color = defaultDiaryTopBarColors.selectedDateTextColor,
        referenceDateContainerColor: Color = defaultDiaryTopBarColors.referenceDateContainerColor,
        referenceDateTextColor: Color = defaultDiaryTopBarColors.referenceDateTextColor
    ) = DiaryTopBarColors(
        containerColor,
        textColor,
        selectedDateContainerColor,
        selectedDateTextColor,
        referenceDateContainerColor,
        referenceDateTextColor
    )

    val shape
        @Composable get() = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(0),
            topStart = CornerSize(0)
        )
}

@Preview
@Preview(
    device = Devices.TABLET
)
@Composable
private fun DiaryTopBarPreview() {
    val namesOfDayOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val referenceDate = LocalDate.of(2024, 12, 17)
    val selectedDate = LocalDate.of(2024, 12, 18)
    val firstDayOfWeek = { _: LocalDate -> LocalDate.of(2024, 12, 15) }
    val formatMonthYear = { _: LocalDate -> "December 2024" }
    val formatFullDate = { _: LocalDate -> "December 18, 2024" }

    FoodYouTheme {
        DiaryTopBar(
            diaryState = rememberDiaryState(
                initialSelectedDate = selectedDate,
                initialReferenceDate = referenceDate,
                zeroDay = LocalDate.ofEpochDay(4)
            ),
            namesOfDayOfWeek = namesOfDayOfWeek,
            getFirstDayOfWeek = firstDayOfWeek,
            formatMonthYear = formatMonthYear,
            scrollBehavior = rememberDiaryTopBarScrollBehavior(),
            formatFullDate = formatFullDate
        )
    }
}
