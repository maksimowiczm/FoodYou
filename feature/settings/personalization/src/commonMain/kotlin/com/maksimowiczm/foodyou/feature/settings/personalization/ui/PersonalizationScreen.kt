package com.maksimowiczm.foodyou.feature.settings.personalization.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.EnergyFormat
import com.maksimowiczm.foodyou.app.ui.shared.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.shared.component.SettingsListItem
import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.PersonalizationScreenViewModel
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PersonalizationScreen(
    onBack: () -> Unit,
    onHomePersonalization: () -> Unit,
    onNutritionFactsPersonalization: () -> Unit,
    onTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PersonalizationScreenViewModel = koinViewModel()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_personalization)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                subtitle = { Text(stringResource(Res.string.description_personalization)) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
        ) {
            item { HomeSettingsListItem(onHomePersonalization) }
            item { PersonalizeNutritionFactsSettingsListItem(onNutritionFactsPersonalization) }
            item {
                EnergyUnitSettingsListItem(
                    unit = viewModel.energyUnit.collectAsStateWithLifecycle().value,
                    onChange = viewModel::setEnergyFormat,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item { ThemeSettingsListItem(onClick = onTheme, modifier = Modifier.fillMaxWidth()) }
            item {
                SecureScreenSettingsListItem(
                    checked = viewModel.secureScreen.collectAsStateWithLifecycle().value,
                    onToggle = viewModel::toggleSecureScreen,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun HomeSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SettingsListItem(
        label = { Text(stringResource(Res.string.headline_home)) },
        onClick = onClick,
        modifier = modifier,
        supportingContent = { Text(stringResource(Res.string.description_home_settings)) },
        icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = null) },
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun PersonalizeNutritionFactsSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsListItem(
        label = { Text(stringResource(Res.string.headline_nutrition_facts)) },
        onClick = onClick,
        modifier = modifier,
        supportingContent = {
            Text(stringResource(Res.string.description_personalize_nutrition_facts_short))
        },
        icon = { Icon(imageVector = Icons.AutoMirrored.Outlined.List, contentDescription = null) },
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun SecureScreenSettingsListItem(
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsListItem(
        label = { Text(stringResource(Res.string.headline_secure_screen)) },
        onClick = { onToggle(!checked) },
        modifier = modifier,
        supportingContent = { Text(stringResource(Res.string.action_prevent_screen_capture)) },
        icon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = null) },
        trailingContent = { Switch(checked = checked, onCheckedChange = null) },
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun EnergyUnitSettingsListItem(
    unit: EnergyFormat,
    onChange: (EnergyFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    val suffix =
        when (unit) {
            EnergyFormat.Kilocalories -> stringResource(Res.string.unit_kcal)
            EnergyFormat.Kilojoules -> stringResource(Res.string.unit_kilojoules)
        }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val menu =
        @Composable {
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_kcal)) },
                    onClick = {
                        onChange(EnergyFormat.Kilocalories)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_kilojoules)) },
                    onClick = {
                        onChange(EnergyFormat.Kilojoules)
                        expanded = false
                    },
                )
            }
        }

    SettingsListItem(
        label = { Text(stringResource(Res.string.headline_energy_unit)) },
        onClick = { expanded = true },
        modifier = modifier,
        supportingContent = { Text(stringResource(Res.string.description_energy_unit)) },
        trailingContent = {
            Box {
                Text(
                    text = suffix,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
                menu()
            }
        },
        icon = { Icon(imageVector = Icons.Outlined.Bolt, contentDescription = null) },
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun ThemeSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SettingsListItem(
        label = { Text(stringResource(Res.string.headline_colors)) },
        onClick = onClick,
        modifier = modifier,
        supportingContent = { Text(stringResource(Res.string.description_colors)) },
        icon = { Icon(imageVector = Icons.Outlined.Palette, contentDescription = null) },
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}
