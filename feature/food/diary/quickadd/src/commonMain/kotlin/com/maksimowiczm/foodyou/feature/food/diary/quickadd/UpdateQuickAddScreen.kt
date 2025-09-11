package com.maksimowiczm.foodyou.feature.food.diary.quickadd

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.fooddiary.domain.entity.ManualDiaryEntryId
import com.maksimowiczm.foodyou.shared.compose.extension.LaunchedCollectWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UpdateQuickAddScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    id: Long,
    modifier: Modifier = Modifier,
) {
    val viewModel: UpdateQuickAddViewModel = koinViewModel { parametersOf(ManualDiaryEntryId(id)) }

    val latestOnSave by rememberUpdatedState(onSave)
    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            QuickAddUiEvent.Saved -> latestOnSave()
        }
    }

    val entry = viewModel.entry.collectAsStateWithLifecycle().value

    if (entry == null) {
        // TODO loading state
        return
    }

    val formState =
        rememberQuickAddFormState(
            name = entry.name,
            energy = entry.nutritionFacts.energy.value,
            proteins = entry.nutritionFacts.proteins.value,
            carbohydrates = entry.nutritionFacts.carbohydrates.value,
            fats = entry.nutritionFacts.fats.value,
        )

    QuickAddScreen(
        onBack = onBack,
        onSave = {
            val name = formState.name.value
            val energy = formState.energy.value ?: 0.0
            val proteins = formState.proteins.value ?: 0.0
            val carbohydrates = formState.carbohydrates.value ?: 0.0
            val fats = formState.fats.value ?: 0.0

            viewModel.updateEntry(
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
