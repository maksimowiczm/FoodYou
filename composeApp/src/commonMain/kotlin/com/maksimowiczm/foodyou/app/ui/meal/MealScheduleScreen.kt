package com.maksimowiczm.foodyou.app.ui.meal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.now
import com.maksimowiczm.foodyou.app.ui.common.extension.plus
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.form.rememberFormField
import com.maksimowiczm.foodyou.app.ui.common.saveable.jsonSaver
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalDateFormatter
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import foodyou.app.generated.resources.*
import io.konform.validation.required
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun MealScheduleScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MealScheduleViewModel = koinInject()
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            MealScheduleEvent.Updated -> onBack()
        }
    }

    if (meals == null) return

    MealScheduleScreen(
        onBack = onBack,
        onSave = { updatedStates ->
            val newMeals =
                updatedStates
                    .filterNot { it.isDeleted }
                    .map { state ->
                        Meal(
                            identity = state.id,
                            name = state.formField.textFieldState.text.toString(),
                            timeWindow = state.timeState.toTimeWindow(),
                        )
                    }
            viewModel.update(newMeals)
        },
        initialMeals = meals,
        modifier = modifier,
    )
}

@Composable
private fun MealScheduleScreen(
    onBack: () -> Unit,
    onSave: (List<MealCardState>) -> Unit,
    initialMeals: List<Meal>,
    modifier: Modifier = Modifier,
) {
    val initialData = remember {
        initialMeals.associateBy({ it.identity }, { it.name to it.timeWindow })
    }
    val seeds =
        rememberSaveable(
            saver =
                Saver(
                    save = { list -> list.map { it.id.toString() } },
                    restore = { saved ->
                        saved.map { MealIdentity(Uuid.parse(it)) }.toMutableStateList()
                    },
                )
        ) {
            initialMeals.map { it.identity }.toMutableStateList()
        }

    val cardStates: List<MealCardState> = seeds.map { id ->
        key(id.id.toString()) {
            val (name, timeWindow) =
                initialData[id]
                    ?: ("" to Meal.TimeWindow.Range(LocalTime.now(), LocalTime.now() + 1.hours))
            rememberMealCardState(
                id = id,
                initialName = name,
                initialTimeWindow = timeWindow,
                isNew = id !in initialData,
            )
        }
    }

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    val isModified by
        remember(cardStates) {
            derivedStateOf {
                cardStates.any { it.isModified || it.isDeleted || it.isNew } ||
                    seeds.map { it.id } != initialMeals.map { it.identity.id }
            }
        }
    val isValid by
        remember(cardStates) {
            derivedStateOf { cardStates.all { it.isValid } }
        }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isModified,
        onBackCompleted = { showDiscardDialog = true },
    )

    if (showDiscardDialog) {
        DiscardChangesDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack)
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            val fromIndex = from.index
            val toIndex = to.index
            if (fromIndex != toIndex) seeds.add(toIndex, seeds.removeAt(fromIndex))
        }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_meals)) },
                subtitle = { Text(stringResource(Res.string.neutral_set_your_meal_schedule)) },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = {
                            if (isModified) showDiscardDialog = true else onBack()
                        }
                    )
                },
                actions = {
                    FilledIconButton(
                        onClick = { onSave(cardStates) },
                        shapes = IconButtonDefaults.shapes(),
                        modifier =
                            Modifier.size(
                                IconButtonDefaults.smallContainerSize(
                                    IconButtonDefaults.IconButtonWidthOption.Wide
                                )
                            ),
                        enabled = isValid,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(Res.string.action_save),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .imePadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = contentPadding.add(8.dp),
        ) {
            itemsIndexed(
                items = cardStates,
                key = { _, it -> it.id.id.toString() },
            ) { i, state ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = state.id.id.toString(),
                    animateItemModifier =
                        Modifier.animateItem(
                            placementSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                        ),
                ) { isDragging ->
                    val visibleState = remember { MutableTransitionState(true) }
                    LaunchedEffect(visibleState.currentState, visibleState.isIdle) {
                        if (visibleState.isIdle && !visibleState.currentState)
                            seeds.remove(state.id)
                    }

                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter =
                            expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()) +
                                fadeIn(),
                        exit =
                            shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()) +
                                fadeOut(),
                    ) {
                        Column {
                            MealCard(
                                state = state,
                                onRemove = {
                                    visibleState.targetState = false
                                    state.isDeleted = true
                                },
                                shape =
                                    MealCardDefaults.shape(
                                        index = i,
                                        lastIndex = cardStates.lastIndex + 1,
                                        isDragging = isDragging,
                                    ),
                                colors =
                                    MealCardDefaults.colors(
                                        status = state.status,
                                        isDragging = isDragging,
                                    ),
                                elevation = MealCardDefaults.elevation(isDragging),
                                modifier =
                                    Modifier.longPressDraggableHandle(
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(
                                                HapticFeedbackType.GestureThresholdActivate
                                            )
                                        },
                                        onDragStopped = {
                                            hapticFeedback.performHapticFeedback(
                                                HapticFeedbackType.GestureEnd
                                            )
                                        },
                                    ),
                            )
                            Spacer(Modifier.height(2.dp))
                        }
                    }
                }
            }
            item {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val cornerDp =
                    animateDpAsState(
                        // We can't use cardStates.isEmpty() as it will contain items while vertical
                        // shrink animation is running before deleting
                        if (isPressed || cardStates.all { it.isNew && it.isDeleted }) 16.dp
                        else 4.dp,
                        MaterialTheme.motionScheme.fastEffectsSpec(),
                    )

                Surface(
                    onClick = { seeds.add(MealIdentity(Uuid.random())) },
                    modifier =
                        Modifier.graphicsLayer {
                            shape =
                                RoundedCornerShape(
                                    topStart = cornerDp.value,
                                    topEnd = cornerDp.value,
                                    bottomEnd = 16.dp,
                                    bottomStart = 16.dp,
                                )
                            clip = true
                        },
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    interactionSource = interactionSource,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(Res.string.action_add_meal),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MealCard(
    state: MealCardState,
    onRemove: (MealIdentity) -> Unit,
    shape: RoundedCornerShape,
    colors: MealCardColors,
    elevation: Dp,
    modifier: Modifier = Modifier,
) {
    val containerColor =
        animateColorAsState(colors.containerColor, MaterialTheme.motionScheme.fastEffectsSpec())
    val contentColor = colors.contentColor
    val timePickerContainerColor =
        animateColorAsState(
            colors.timePickerContainerColor,
            MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val timePickerContentColor = colors.timePickerContentColor
    val alpha =
        animateFloatAsState(
            if (state.status == MealCardStatus.Deleted) .25f else 1f,
            MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val elevation = animateDpAsState(elevation, MaterialTheme.motionScheme.fastSpatialSpec())

    Box(
        modifier =
            modifier
                .graphicsLayer {
                    this.shadowElevation = elevation.value.toPx()
                    this.shape = shape
                    this.clip = true
                }
                .drawBehind {
                    drawRect(color = containerColor.value)
                }
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    BasicTextField(
                        state = state.formField.textFieldState,
                        modifier =
                            Modifier.defaultMinSize(minWidth = 150.dp)
                                .width(IntrinsicSize.Min)
                                .weight(1f, false)
                                .graphicsLayer { this.alpha = alpha.value },
                        enabled = !state.isDeleted,
                        textStyle =
                            LocalTextStyle.current
                                .merge(MaterialTheme.typography.headlineMedium)
                                .merge(LocalContentColor.current),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        decorator = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Column(modifier = Modifier.weight(1f, false)) {
                                    Box(modifier = Modifier.padding(horizontal = 4.dp)) { it() }
                                    HorizontalDivider(
                                        color =
                                            if (state.isValid) MaterialTheme.colorScheme.outline
                                            else MaterialTheme.colorScheme.error
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                                    tint =
                                        if (state.isValid) LocalContentColor.current
                                        else MaterialTheme.colorScheme.error,
                                )
                            }
                        },
                    )
                    IconButton(
                        onClick = {
                            when {
                                state.isNew -> onRemove(state.id)
                                state.status == MealCardStatus.Normal -> state.isDeleted = true
                                state.status == MealCardStatus.Modified -> state.reset()
                                state.status == MealCardStatus.Deleted -> state.isDeleted = false
                            }
                        },
                        shapes = IconButtonDefaults.shapes(),
                        modifier =
                            Modifier.padding(start = 8.dp)
                                .size(IconButtonDefaults.extraSmallContainerSize()),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor),
                    ) {
                        Icon(
                            imageVector =
                                if (state.isNew) Icons.Outlined.Delete
                                else if (
                                    state.status == MealCardStatus.Modified ||
                                        state.status == MealCardStatus.Deleted
                                )
                                    Icons.AutoMirrored.Outlined.Undo
                                else Icons.Outlined.Delete,
                            contentDescription =
                                if (state.isNew) stringResource(Res.string.action_delete)
                                else if (
                                    state.status == MealCardStatus.Modified ||
                                        state.status == MealCardStatus.Deleted
                                )
                                    stringResource(Res.string.action_undo)
                                else stringResource(Res.string.action_delete),
                            modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                        )
                    }
                }
                AnimatedVisibility(
                    visible = state.timeState !is TimeState.AllDay,
                    enter =
                        expandVertically(MaterialTheme.motionScheme.fastEffectsSpec()) +
                            fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
                    exit =
                        shrinkVertically(MaterialTheme.motionScheme.fastEffectsSpec()) +
                            fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
                ) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        MealTimePicker(
                            state = state.timeState,
                            onStateChange = { state.timeState = it },
                            enabled = !state.isDeleted,
                            containerColor = timePickerContainerColor.value,
                            contentColor = timePickerContentColor,
                            modifier =
                                Modifier.fillMaxWidth().graphicsLayer { this.alpha = alpha.value },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier =
                        Modifier.clickable(
                                interactionSource = null,
                                indication = null,
                                enabled = !state.isDeleted,
                                onClick = { state.timeState = state.timeState.flip() },
                            )
                            .graphicsLayer { this.alpha = alpha.value },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = state.timeState is TimeState.AllDay,
                        onCheckedChange = null,
                        enabled = !state.isDeleted,
                        colors = colors.switchColors,
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = stringResource(Res.string.headline_all_day),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun MealTimePicker(
    state: TimeState,
    onStateChange: (TimeState) -> Unit,
    enabled: Boolean,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = LocalDateFormatter.current

    val showTimePickerState = rememberSaveable {
        mutableStateOf<TimePicker?>(null)
    }
    val showTimePicker = showTimePickerState.value
    if (showTimePicker != null) {
        val timePickerState =
            when (showTimePicker) {
                TimePicker.Start ->
                    rememberTimePickerState(
                        initialHour = state.startTime.hour,
                        initialMinute = state.startTime.minute,
                    )

                TimePicker.End ->
                    rememberTimePickerState(
                        initialHour = state.endTime.hour,
                        initialMinute = state.endTime.minute,
                    )
            }

        val onConfirm = {
            showTimePickerState.value = null
            onStateChange(
                when (showTimePicker) {
                    TimePicker.Start ->
                        TimeState.TimeRange(
                            startTime =
                                LocalTime(
                                    hour = timePickerState.hour,
                                    minute = timePickerState.minute,
                                ),
                            endTime = state.endTime,
                        )

                    TimePicker.End ->
                        TimeState.TimeRange(
                            startTime = state.startTime,
                            endTime =
                                LocalTime(
                                    hour = timePickerState.hour,
                                    minute = timePickerState.minute,
                                ),
                        )
                }
            )
        }

        TimePickerDialog(
            onDismissRequest = { showTimePickerState.value = null },
            confirmButton = {
                TextButton(onClick = onConfirm, shapes = ButtonDefaults.shapes()) {
                    Text(text = stringResource(Res.string.action_confirm))
                }
            },
            title = {},
            dismissButton = {
                TextButton(
                    onClick = { showTimePickerState.value = null },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(text = stringResource(Res.string.action_cancel))
                }
            },
        ) {
            TimePicker(timePickerState)
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Card(
            onClick = { showTimePickerState.value = TimePicker.Start },
            enabled = enabled,
            colors =
                CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                ),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = dateFormatter.formatTime(state.startTime),
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        Text(
            text = stringResource(Res.string.en_dash),
            style = MaterialTheme.typography.headlineSmall,
        )
        Card(
            onClick = { showTimePickerState.value = TimePicker.End },
            enabled = enabled,
            colors =
                CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                ),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = dateFormatter.formatTime(state.endTime),
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

private class MealCardState(
    val id: MealIdentity,
    val formField: FormField,
    private val initialName: String,
    timeStateState: MutableState<TimeState>,
    private val initialTimeState: TimeState,
    isDeletedState: MutableState<Boolean>,
    val isNew: Boolean,
) {
    var timeState by timeStateState
    var isDeleted by isDeletedState
    val isValid by derivedStateOf { formField.isValid || isDeleted }
    val isModified by derivedStateOf {
        formField.isModified || !timeState.isEquivalentTo(initialTimeState) || isDeleted
    }
    val status by derivedStateOf {
        if (isDeleted) MealCardStatus.Deleted
        else if (formField.isModified || !timeState.isEquivalentTo(initialTimeState))
            MealCardStatus.Modified
        else MealCardStatus.Normal
    }

    fun reset() {
        formField.textFieldState.setTextAndPlaceCursorAtEnd(initialName)
        timeState = initialTimeState
    }
}

@Immutable
@Serializable
private sealed interface TimeState {
    val startTime: LocalTime
    val endTime: LocalTime

    @Immutable
    @Serializable
    data class AllDay(
        override val startTime: LocalTime,
        override val endTime: LocalTime,
    ) : TimeState {
        override fun flip(): TimeState = TimeRange(startTime, endTime)
    }

    @Immutable
    @Serializable
    data class TimeRange(
        override val startTime: LocalTime,
        override val endTime: LocalTime,
    ) : TimeState {
        override fun flip(): TimeState = AllDay(startTime, endTime)
    }

    fun flip(): TimeState

    fun isEquivalentTo(other: TimeState): Boolean =
        when (this) {
            is AllDay -> other is AllDay
            is TimeRange ->
                other is TimeRange && startTime == other.startTime && endTime == other.endTime
        }
}

private fun Meal.TimeWindow.toTimeState() =
    when (this) {
        Meal.TimeWindow.AllDay -> TimeState.AllDay(LocalTime.now(), LocalTime.now() + 1.hours)
        is Meal.TimeWindow.Range -> TimeState.TimeRange(start, end)
    }

private fun TimeState.toTimeWindow() =
    when (this) {
        is TimeState.AllDay -> Meal.TimeWindow.AllDay
        is TimeState.TimeRange -> Meal.TimeWindow.Range(startTime, endTime)
    }

@Composable
private fun rememberMealCardState(
    id: MealIdentity,
    initialName: String,
    initialTimeWindow: Meal.TimeWindow,
    isNew: Boolean,
): MealCardState {
    val formField =
        rememberFormField(defaultValue = initialName) {
            required {}
        }
    val initialTimeState =
        rememberSaveable(initialTimeWindow, saver = jsonSaver()) {
            initialTimeWindow.toTimeState()
        }
    val timeStateState =
        rememberSerializable(initialTimeState) {
            mutableStateOf(initialTimeState)
        }
    val isDeletedState = rememberSaveable { mutableStateOf(false) }

    return remember(
        id,
        formField,
        initialName,
        timeStateState,
        initialTimeState,
        isDeletedState,
        isNew,
    ) {
        MealCardState(
            id,
            formField,
            initialName,
            timeStateState,
            initialTimeState,
            isDeletedState,
            isNew,
        )
    }
}

private enum class TimePicker {
    Start,
    End,
}

private enum class MealCardStatus {
    Normal,
    Modified,
    Deleted,
}

@Immutable
private class MealCardColors(
    val containerColor: Color,
    val contentColor: Color,
    val switchColors: SwitchColors,
    val timePickerContainerColor: Color,
    val timePickerContentColor: Color,
)

private object MealCardDefaults {
    @Composable
    fun colors(status: MealCardStatus, isDragging: Boolean): MealCardColors =
        when {
            isDragging ->
                MealCardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    switchColors =
                        SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onTertiary,
                            checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            uncheckedTrackColor = Color.Transparent,
                            uncheckedBorderColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                    timePickerContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    timePickerContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )

            status == MealCardStatus.Modified ->
                MealCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    switchColors =
                        SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onTertiary,
                            checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                            uncheckedTrackColor = Color.Transparent,
                            uncheckedBorderColor = MaterialTheme.colorScheme.secondary,
                        ),
                    timePickerContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    timePickerContentColor = MaterialTheme.colorScheme.onSurface,
                )

            status == MealCardStatus.Deleted ->
                MealCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    switchColors = SwitchDefaults.colors(uncheckedTrackColor = Color.Transparent),
                    timePickerContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    timePickerContentColor = MaterialTheme.colorScheme.onSurface,
                )

            else ->
                MealCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    switchColors = SwitchDefaults.colors(uncheckedTrackColor = Color.Transparent),
                    timePickerContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    timePickerContentColor = MaterialTheme.colorScheme.onSurface,
                )
        }

    @Composable
    fun shape(
        index: Int,
        lastIndex: Int,
        isDragging: Boolean,
    ): RoundedCornerShape {
        val topCorner =
            animateDpAsState(
                if (index == 0 || isDragging) 16.dp else 4.dp,
                MaterialTheme.motionScheme.fastSpatialSpec(),
            )
        val bottomCorner =
            animateDpAsState(
                if (index == lastIndex || isDragging) 16.dp else 4.dp,
                MaterialTheme.motionScheme.fastSpatialSpec(),
            )
        return RoundedCornerShape(
            topStart = topCorner.value,
            topEnd = topCorner.value,
            bottomEnd = bottomCorner.value,
            bottomStart = bottomCorner.value,
        )
    }

    fun elevation(isDragging: Boolean) = if (isDragging) 8.dp else 0.dp
}
