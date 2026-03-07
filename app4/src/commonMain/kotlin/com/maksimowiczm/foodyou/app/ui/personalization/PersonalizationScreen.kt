package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
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
import com.maksimowiczm.foodyou.account.domain.EnergyFormat
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PersonalizationScreen(
    onBack: () -> Unit,
    onHome: () -> Unit,
    onNutritionFacts: () -> Unit,
    onColors: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PersonalizationViewModel = koinViewModel()

    val secureScreen by viewModel.secureScreen.collectAsStateWithLifecycle()
    val energyFormat by viewModel.energyFormat.collectAsStateWithLifecycle()

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
            contentPadding = paddingValues.add(bottom = 8.dp),
        ) {
            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_home)) },
                    modifier = Modifier.clickable { onHome() },
                    supportingContent = {
                        Text(stringResource(Res.string.description_home_settings))
                    },
                    leadingContent = {
                        Icon(imageVector = Icons.Outlined.Home, contentDescription = null)
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_nutrition_facts)) },
                    modifier = Modifier.clickable { onNutritionFacts() },
                    supportingContent = {
                        Text(
                            stringResource(Res.string.description_personalize_nutrition_facts_short)
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.List,
                            contentDescription = null,
                        )
                    },
                )
            }
            item {
                val suffix =
                    when (energyFormat) {
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
                                    viewModel.updateEnergyFormat(EnergyFormat.Kilocalories)
                                    expanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.unit_kilojoules)) },
                                onClick = {
                                    viewModel.updateEnergyFormat(EnergyFormat.Kilojoules)
                                    expanded = false
                                },
                            )
                        }
                    }
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_energy_unit)) },
                    modifier = Modifier.clickable { expanded = true },
                    supportingContent = {
                        Text(stringResource(Res.string.description_energy_unit))
                    },
                    leadingContent = {
                        Icon(imageVector = Icons.Outlined.Bolt, contentDescription = null)
                    },
                    trailingContent = {
                        Box {
                            Text(
                                text = suffix,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            menu()
                        }
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_colors)) },
                    modifier = Modifier.clickable { onColors() },
                    supportingContent = { Text(stringResource(Res.string.description_colors)) },
                    leadingContent = {
                        Icon(imageVector = Icons.Outlined.Palette, contentDescription = null)
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.headline_secure_screen)) },
                    modifier = Modifier.clickable { viewModel.updateSecureScreen(!secureScreen) },
                    supportingContent = {
                        Text(stringResource(Res.string.action_prevent_screen_capture))
                    },
                    leadingContent = {
                        Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
                    },
                    trailingContent = { Switch(checked = secureScreen, onCheckedChange = null) },
                )
            }
        }
    }
}
