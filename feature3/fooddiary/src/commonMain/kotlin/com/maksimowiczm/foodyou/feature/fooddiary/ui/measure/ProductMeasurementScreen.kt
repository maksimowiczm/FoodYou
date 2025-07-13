package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ext.minus
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.Product
import com.maksimowiczm.foodyou.feature.food.ui.Icon
import com.maksimowiczm.foodyou.feature.food.ui.stringResource
import com.maksimowiczm.foodyou.feature.fooddiary.data.Meal
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ProductMeasurementScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMeasure: (Measurement, mealId: Long, date: LocalDate) -> Unit,
    product: Product,
    today: LocalDate,
    selectedDate: LocalDate,
    meals: List<Meal>,
    selectedMeal: Meal,
    suggestions: List<Measurement>,
    possibleTypes: List<com.maksimowiczm.foodyou.feature.measurement.data.Measurement>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    selectedMeasurement: Measurement = suggestions.first()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val state = rememberProductMeasurementFormState(
        today = today,
        possibleDates = setOf(selectedDate, today.minus(1.days), today),
        selectedDate = selectedDate,
        meals = meals.toSet(),
        selectedMeal = selectedMeal,
        suggestions = suggestions.toSet(),
        possibleTypes = possibleTypes.toSet(),
        selectedMeasurement = selectedMeasurement
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text(product.headline) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    Menu(
                        onEdit = onEdit,
                        onDelete = onDelete
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            MediumExtendedFloatingActionButton(
                onClick = {
                    if (state.isValid) {
                        onMeasure(
                            state.measurementState.measurement,
                            meals.first { it.name == state.mealsState.selectedMeal }.id,
                            state.dateState.selectedDate
                        )
                    }
                },
                modifier = Modifier.animateFloatingActionButton(
                    visible = !animatedVisibilityScope.transition.isRunning && state.isValid,
                    alignment = Alignment.Companion.BottomEnd
                ),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(Res.string.action_save)
                    )
                },
                text = {
                    Text(stringResource(Res.string.action_save))
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 8.dp
                )
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
                .add(vertical = 8.dp)
                .add(bottom = 80.dp + 24.dp) // FAB
        ) {
            item {
                HorizontalDivider()
            }

            item {
                ProductMeasurementForm(
                    state = state,
                    product = product,
                    modifier = Modifier.padding(
                        bottom = 8.dp
                    )
                )
            }

            when (val note = product.note) {
                null -> Unit
                else -> {
                    item {
                        HorizontalDivider()
                        Note(
                            note = note,
                            modifier = Modifier.padding(
                                vertical = 16.dp,
                                horizontal = 8.dp
                            )
                        )
                    }
                }
            }

            item {
                HorizontalDivider()

                val clipboardManger = LocalClipboardManager.current
                val sourceStr = stringResource(Res.string.headline_source)

                Source(
                    source = product.source,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                val url = product.source.url
                                if (url != null) {
                                    clipboardManger.copy(
                                        label = sourceStr,
                                        text = url
                                    )
                                }
                            }
                        )
                        .padding(
                            vertical = 16.dp,
                            horizontal = 8.dp
                        )
                )
            }
        }
    }
}

@Composable
private fun Menu(onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDelete = onDelete
        )
    }

    Box(modifier) {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(Res.string.action_show_more)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_edit)) },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_delete)) },
                onClick = {
                    expanded = false
                    showDeleteDialog = true
                }
            )
        }
    }
}

@Composable
private fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDelete
            ) {
                Text(stringResource(Res.string.action_delete))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(Res.string.headline_delete_product))
        },
        text = {
            Text(stringResource(Res.string.description_delete_product))
        }
    )
}

@Composable
private fun Note(note: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_note),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = note,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Source(source: FoodSource, modifier: Modifier = Modifier) {
    val url = source.url

    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_source),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            source.type.Icon()

            Column {
                Text(
                    text = source.type.stringResource(),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (url != null) {
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
