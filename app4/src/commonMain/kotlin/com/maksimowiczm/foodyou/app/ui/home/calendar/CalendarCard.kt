package com.maksimowiczm.foodyou.app.ui.home.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.confirm
import com.maksimowiczm.foodyou.app.ui.common.extension.minus
import com.maksimowiczm.foodyou.app.ui.common.extension.now
import com.maksimowiczm.foodyou.app.ui.common.extension.segmentFrequentTick
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalClock
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalDateFormatter
import com.maksimowiczm.foodyou.app.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.app.ui.home.FoodYouHomeCardDefaults
import com.maksimowiczm.foodyou.app.ui.home.HomeState
import com.maksimowiczm.foodyou.common.clock.domain.observeDate
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun CalendarCard(homeState: HomeState, modifier: Modifier = Modifier) {
    val clock = LocalClock.current
    val today by clock.observeDate().collectAsStateWithLifecycle(LocalDate.now())

    val state =
        rememberCalendarCardState(referenceDate = today, selectedDate = homeState.selectedDate)

    LaunchedEffect(state.selectedDate) {
        val date = state.selectedDate
        if (date != homeState.selectedDate) {
            homeState.selectDate(date)
        }
    }

    CalendarCard(modifier = modifier, state = state)
}

@Composable
private fun CalendarCard(
    modifier: Modifier = Modifier,
    state: CalendarCardState = rememberCalendarCardState(),
) {
    val hapticFeedback = LocalHapticFeedback.current

    // Tick when user scrolls
    LaunchedEffect(state) {
        combine(
                snapshotFlow { state.listState.isScrollInProgress },
                snapshotFlow { state.listState.firstVisibleItemIndex },
            ) { isScrollInProgress, _ ->
                if (isScrollInProgress) hapticFeedback.segmentFrequentTick()
            }
            .launchIn(this)
    }

    val scope = rememberCoroutineScope()
    val dateFormatter = LocalDateFormatter.current
    val buttonHeight = ButtonDefaults.ExtraSmallContainerHeight

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    if (showDatePicker) {
        CalendarCardDatePickerDialog(
            calendarState = state,
            onDismissRequest = { showDatePicker = false },
            onSelectDate = { date ->
                scope.launch {
                    state.selectDate(date)
                    hapticFeedback.confirm()
                    state.snapScrollTo(date)
                }
            },
        )
    }

    FoodYouHomeCard(onClick = { showDatePicker = true }, modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(buttonHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = dateFormatter.formatMonthYear(state.firstVisibleDate),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.weight(1f))
                AnimatedVisibility(
                    !state.referenceDateVisible || state.selectedDate != state.referenceDate,
                    enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                    exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                state.selectDate(state.referenceDate)
                                hapticFeedback.confirm()
                                state.animateScrollTo(state.referenceDate)
                            }
                        },
                        shapes = ButtonDefaults.shapesFor(buttonHeight),
                        modifier = Modifier.height(buttonHeight),
                        contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight),
                    ) {
                        Text(
                            text = stringResource(Res.string.action_today),
                            style = ButtonDefaults.textStyleFor(buttonHeight),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                state = state.listState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.daysCount) { index ->
                    val date = LocalDate.fromEpochDays(index)

                    CalendarLazyRowItem(
                        date = date,
                        state = state,
                        onClick = {
                            state.selectDate(date)
                            hapticFeedback.confirm()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarLazyRowItem(
    date: LocalDate,
    state: CalendarCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor by
        animateColorAsState(
            when (date) {
                state.selectedDate -> MaterialTheme.colorScheme.primary
                state.referenceDate -> MaterialTheme.colorScheme.secondaryContainer
                else -> FoodYouHomeCardDefaults.color
            }
        )
    val contentColor by
        animateColorAsState(
            when (date) {
                state.selectedDate -> MaterialTheme.colorScheme.onPrimary
                state.referenceDate -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> FoodYouHomeCardDefaults.contentColor
            }
        )

    CalendarDayItem(
        date = date,
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
    )
}

@Composable
private fun CalendarDayItem(
    date: LocalDate,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = LocalDateFormatter.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerRadius by
        animateDpAsState(
            targetValue = if (isPressed) 8.dp else 12.dp,
            animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        )

    Surface(
        onClick = onClick,
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(cornerRadius),
    ) {
        Column(
            modifier = Modifier.minimumInteractiveComponentSize().size(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                val dayOfWeek = (date.dayOfWeek.isoDayNumber - 1) % 7
                Text(dateFormatter.weekDayNamesShort[dayOfWeek])
                Text(date.day.toString())
            }
        }
    }
}

@Composable
private fun CalendarCardDatePickerDialog(
    calendarState: CalendarCardState,
    onDismissRequest: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
) {
    val state = calendarState.rememberDatePickerState()
    val scope = rememberCoroutineScope()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let {
                        val date =
                            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date

                        onSelectDate(date)
                    }
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(Res.string.positive_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(Res.string.action_cancel))
            }
        },
    ) {
        DatePicker(
            state = state,
            // It won't fit on small screens, so we need to scroll
            modifier = Modifier.verticalScroll(rememberScrollState()),
        )
    }
}

@Preview
@Composable
private fun CalendarCardPreview() {
    PreviewFoodYouTheme {
        CalendarCard(
            state =
                rememberCalendarCardState(
                    referenceDate = LocalDate.now(),
                    selectedDate = LocalDate.now() - 1.days,
                )
        )
    }
}

@Preview
@Composable
private fun CalendarDayItemPreview() {
    PreviewFoodYouTheme {
        Row {
            CalendarDayItem(
                date = LocalDate.now(),
                onClick = {},
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
            CalendarDayItem(
                date = LocalDate.now(),
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
