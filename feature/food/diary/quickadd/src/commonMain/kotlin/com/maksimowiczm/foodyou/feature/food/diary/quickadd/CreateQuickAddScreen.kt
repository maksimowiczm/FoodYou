package com.maksimowiczm.foodyou.feature.food.diary.quickadd

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.shared.compose.extension.LaunchedCollectWithLifecycle
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CreateQuickAddScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    mealId: Long,
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    val viewModel: CreateQuickAddViewModel = koinViewModel {
        parametersOf(date.toEpochDays(), mealId)
    }

    val latestOnSave by rememberUpdatedState(onSave)
    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            QuickAddUiEvent.Saved -> latestOnSave()
        }
    }

    val formState = rememberQuickAddFormState()

    QuickAddScreen(
        onBack = onBack,
        onSave = {
            val name = formState.name.value
            val energy = formState.energy.value ?: 0.0
            val proteins = formState.proteins.value ?: 0.0
            val carbohydrates = formState.carbohydrates.value ?: 0.0
            val fats = formState.fats.value ?: 0.0

            viewModel.addEntry(
                name = name,
                energy = energy,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
            )
        },
        modifier = modifier,
        formState = formState,
    )
}
