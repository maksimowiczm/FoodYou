package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementForm
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementFormState
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.AdvancedMeasurementSummary
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateMeasurementScreen(
    state: AdvancedMeasurementFormState,
    food: Food,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(food.headline) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            MediumExtendedFloatingActionButton(
                modifier = Modifier.animateFloatingActionButton(
                    visible = state.isValid,
                    alignment = Alignment.BottomEnd
                ),
                onClick = {
                    if (state.isValid) {
                        onSave()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
                Spacer(Modifier.width(12.dp))
                Text(stringResource(Res.string.action_save))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                HorizontalDivider()
            }

            item {
                AdvancedMeasurementForm(
                    state = state
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                val measurement = state.measurement ?: Measurement.Gram(100f)
                val weight = measurement.weight(food) ?: 100f
                val nutritionFacts = food.nutritionFacts * weight / 100f

                AdvancedMeasurementSummary(
                    nutritionFacts = nutritionFacts,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp + 80.dp + 16.dp))
            }
        }
    }
}
