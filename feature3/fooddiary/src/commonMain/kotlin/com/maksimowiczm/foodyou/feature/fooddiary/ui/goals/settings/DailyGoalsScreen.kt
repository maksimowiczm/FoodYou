package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.settings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycleInitialBlock
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField
import com.maksimowiczm.foodyou.feature.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.GoalsPreference
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DailyGoalsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val goalsPreference = userPreference<GoalsPreference>()
    val weeklyGoals by goalsPreference.collectAsStateWithLifecycleInitialBlock()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_daily_goals)) },
                navigationIcon = { ArrowBackIconButton(onBack) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentPadding = paddingValues.add(vertical = 8.dp)
        ) {
            item {
                DailyGoalsForm(
                    goal = weeklyGoals.monday,
                    onChange = {
                        // TODO
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DailyGoalsForm(
    goal: DailyGoal,
    onChange: (DailyGoal) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    var useDistribution by rememberSaveable { mutableStateOf(false) }

    val proteins = goal[NutritionFactsField.Proteins].toFloat()
    val carbohydrates = goal[NutritionFactsField.Carbohydrates].toFloat()
    val fats = goal[NutritionFactsField.Fats].toFloat()
    val energy = goal[NutritionFactsField.Energy].roundToInt()

    val sliderState = rememberMacroInputSliderFormState(
        proteins = NutrientsHelper.proteinsPercentage(energy, proteins) * 100f,
        carbohydrates = NutrientsHelper.carbohydratesPercentage(energy, carbohydrates) * 100f,
        fats = NutrientsHelper.fatsPercentage(energy, fats) * 100f,
        energy = energy
    )

    val weightState = rememberMacroWeightInputFormState(
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        energy = energy
    )

    val additionalState = rememberAdditionalGoalsFormState(goal)

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
                .clickable {
                    // TODO
                }
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Checkbox(
                modifier = Modifier.padding(
                    vertical = 16.dp
                ),
                checked = false,
                onCheckedChange = null
            )
            Text(
                text = "Set separate goals for each day",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Set goals",
            modifier = Modifier.padding(contentPadding),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        DistributionButtons(
            useDistribution = useDistribution,
            onUseDistributionChange = { useDistribution = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        )
        if (useDistribution) {
            MacroInputSliderForm(
                state = sliderState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        } else {
            MacroWeightInputForm(
                state = weightState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        AdditionalGoalsForm(
            state = additionalState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DistributionButtons(
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
            onCheckedChange = { onUseDistributionChange(!it) },
            modifier = Modifier
                .height(56.dp)
                .semantics { role = Role.RadioButton }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_weight),
                contentDescription = null
            )
        }
        ToggleButton(
            checked = useDistribution,
            onCheckedChange = { onUseDistributionChange(it) },
            modifier = Modifier
                .height(56.dp)
                .semantics { role = Role.RadioButton }
        ) {
            Icon(
                imageVector = Icons.Outlined.Percent,
                contentDescription = null
            )
        }
    }
}
