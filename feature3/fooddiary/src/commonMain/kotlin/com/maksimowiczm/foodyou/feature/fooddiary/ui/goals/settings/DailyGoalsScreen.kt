package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycleInitialBlock
import com.maksimowiczm.foodyou.core.preferences.setBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.BackHandler
import com.maksimowiczm.foodyou.core.ui.DiscardDialog
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.GoalsPreference
import foodyou.app.generated.resources.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DailyGoalsScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goalsPreference = userPreference<GoalsPreference>()
    val weeklyGoals by goalsPreference.collectAsStateWithLifecycleInitialBlock()

    val state = rememberDailyGoalsState(weeklyGoals)

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    BackHandler(state.isModified) {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onDiscard = onBack
        ) {
            Text(stringResource(Res.string.question_discard_changes))
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_daily_goals)) },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = {
                            if (state.isModified) {
                                showDiscardDialog = true
                            } else {
                                onBack()
                            }
                        }
                    )
                },
                actions = {
                    FilledIconButton(
                        onClick = {
                            val weeklyGoals = state.intoWeeklyGoals()
                            goalsPreference.setBlocking(weeklyGoals)
                            onSave()
                        },
                        enabled = state.isValid
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp)
        ) {
            item {
                DayPicker(
                    useSeparateGoals = state.useSeparateGoals,
                    onUseSeparateGoalsChange = { state.useSeparateGoals = it },
                    selectedDay = state.selectedDay,
                    onSelectedDayChange = { state.selectedDay = it },
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                DailyGoalsForm(
                    state = state.selectedDayGoals,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DailyGoalsForm(
    state: DayGoalsState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.action_set_goals),
            modifier = Modifier.padding(contentPadding),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        WeightOrPercentageToggle(
            useDistribution = state.useDistribution,
            onUseDistributionChange = { state.useDistribution = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .padding(vertical = 8.dp)
        )
        if (state.useDistribution) {
            MacroInputSliderForm(
                state = state.sliderState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        } else {
            MacroWeightInputForm(
                state = state.weightState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        AdditionalGoalsForm(
            state = state.additionalState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DayPicker(
    useSeparateGoals: Boolean,
    onUseSeparateGoalsChange: (Boolean) -> Unit,
    selectedDay: Int,
    onSelectedDayChange: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = "Pick the days",
            modifier = Modifier.padding(contentPadding),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUseSeparateGoalsChange(!useSeparateGoals) }
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Checkbox(
                modifier = Modifier.padding(
                    vertical = 16.dp
                ),
                checked = useSeparateGoals,
                onCheckedChange = null
            )
            Text(
                text = "Set separate goals for each day",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        AnimatedVisibility(useSeparateGoals) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                contentPadding = contentPadding
            ) {
                item {
                    val dateFormatter = LocalDateFormatter.current
                    val weekDayNamesShort = dateFormatter.weekDayNamesShort

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            ButtonGroupDefaults.ConnectedSpaceBetween,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        weekDayNamesShort.forEachIndexed { i, name ->
                            ToggleButton(
                                checked = selectedDay == i,
                                onCheckedChange = {
                                    // TODO We don't know if 0 is Sunday or Monday
                                    onSelectedDayChange(i)
                                },
                                modifier = Modifier.semantics { role = Role.RadioButton },
                                shapes = when (i) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    weekDayNamesShort.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                }
                            ) {
                                Text(name)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WeightOrPercentageToggle(
    useDistribution: Boolean,
    onUseDistributionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            ButtonGroupDefaults.ConnectedSpaceBetween,
            Alignment.CenterHorizontally
        )
    ) {
        ToggleButton(
            checked = !useDistribution,
            onCheckedChange = { onUseDistributionChange(false) },
            modifier = Modifier
                .height(56.dp)
                .semantics { role = Role.RadioButton }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_weight),
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Weight")
        }
        ToggleButton(
            checked = useDistribution,
            onCheckedChange = { onUseDistributionChange(true) },
            modifier = Modifier
                .height(56.dp)
                .semantics { role = Role.RadioButton }
        ) {
            Icon(
                imageVector = Icons.Outlined.Percent,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Percentage")
        }
    }
}
