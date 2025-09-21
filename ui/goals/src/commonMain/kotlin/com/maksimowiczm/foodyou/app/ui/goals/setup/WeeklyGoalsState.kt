package com.maksimowiczm.foodyou.app.ui.goals.setup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.goals.domain.entity.WeeklyGoals

internal class WeeklyGoalsState(
    val monday: DailyGoalsFormState,
    val tuesday: DailyGoalsFormState,
    val wednesday: DailyGoalsFormState,
    val thursday: DailyGoalsFormState,
    val friday: DailyGoalsFormState,
    val saturday: DailyGoalsFormState,
    val sunday: DailyGoalsFormState,
    selectedDayState: MutableState<Int>,
    useSeparateGoalsState: MutableState<Boolean>,
    isModifiedState: State<Boolean>,
) {
    var selectedDay by selectedDayState
    var useSeparateGoals by useSeparateGoalsState

    val isValid by derivedStateOf {
        if (!useSeparateGoals) {
            monday.isValid
        } else {
            monday.isValid &&
                tuesday.isValid &&
                wednesday.isValid &&
                thursday.isValid &&
                friday.isValid &&
                saturday.isValid &&
                sunday.isValid
        }
    }

    val isModified by isModifiedState

    val selectedDayGoals: DailyGoalsFormState by derivedStateOf {
        when (selectedDay) {
            0 -> monday
            1 -> tuesday
            2 -> wednesday
            3 -> thursday
            4 -> friday
            5 -> saturday
            6 -> sunday
            else -> error("Invalid day index: $selectedDay")
        }
    }

    fun intoWeeklyGoals() =
        if (!useSeparateGoals) {
            val dailyGoals = monday.intoDailyGoals()
            WeeklyGoals(
                monday = dailyGoals,
                tuesday = dailyGoals,
                wednesday = dailyGoals,
                thursday = dailyGoals,
                friday = dailyGoals,
                saturday = dailyGoals,
                sunday = dailyGoals,
                useSeparateGoals = false,
            )
        } else {
            WeeklyGoals(
                monday = monday.intoDailyGoals(),
                tuesday = tuesday.intoDailyGoals(),
                wednesday = wednesday.intoDailyGoals(),
                thursday = thursday.intoDailyGoals(),
                friday = friday.intoDailyGoals(),
                saturday = saturday.intoDailyGoals(),
                sunday = sunday.intoDailyGoals(),
                useSeparateGoals = true,
            )
        }
}

@Composable
internal fun rememberWeeklyGoalsState(weeklyGoals: WeeklyGoals): WeeklyGoalsState {
    val monday = rememberDailyGoalsFormState(weeklyGoals.monday)
    val tuesday = rememberDailyGoalsFormState(weeklyGoals.tuesday)
    val wednesday = rememberDailyGoalsFormState(weeklyGoals.wednesday)
    val thursday = rememberDailyGoalsFormState(weeklyGoals.thursday)
    val friday = rememberDailyGoalsFormState(weeklyGoals.friday)
    val saturday = rememberDailyGoalsFormState(weeklyGoals.saturday)
    val sunday = rememberDailyGoalsFormState(weeklyGoals.sunday)

    val selectedDayState = rememberSaveable { mutableIntStateOf(0) }

    val useSeparateGoalsState = rememberSaveable { mutableStateOf(weeklyGoals.useSeparateGoals) }

    LaunchedEffect(useSeparateGoalsState.value) {
        if (!useSeparateGoalsState.value) {
            selectedDayState.value = 0
        }
    }

    val isModified = remember {
        derivedStateOf {
            if (!useSeparateGoalsState.value) {
                monday.isModified || useSeparateGoalsState.value != weeklyGoals.useSeparateGoals
            } else {
                monday.isModified ||
                    tuesday.isModified ||
                    wednesday.isModified ||
                    thursday.isModified ||
                    friday.isModified ||
                    saturday.isModified ||
                    sunday.isModified ||
                    useSeparateGoalsState.value != weeklyGoals.useSeparateGoals
            }
        }
    }

    return remember(
        monday,
        tuesday,
        wednesday,
        thursday,
        friday,
        saturday,
        sunday,
        selectedDayState,
        useSeparateGoalsState,
        isModified,
    ) {
        WeeklyGoalsState(
            monday = monday,
            tuesday = tuesday,
            wednesday = wednesday,
            thursday = thursday,
            friday = friday,
            saturday = saturday,
            sunday = sunday,
            selectedDayState = selectedDayState,
            useSeparateGoalsState = useSeparateGoalsState,
            isModifiedState = isModified,
        )
    }
}
