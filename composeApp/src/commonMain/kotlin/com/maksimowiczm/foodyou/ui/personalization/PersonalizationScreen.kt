package com.maksimowiczm.foodyou.ui.personalization

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.setBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.SettingsListItem
import com.maksimowiczm.foodyou.preferences.HideContent
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PersonalizationScreen(
    onBack: () -> Unit,
    onHomePersonalization: () -> Unit,
    onNutritionFactsPersonalization: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_personalization)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                subtitle = { Text(stringResource(Res.string.description_personalization)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                HomeSettingsListItem(onHomePersonalization)
            }
            item {
                PersonalizeNutritionFactsSettingsListItem(onNutritionFactsPersonalization)
            }
            item {
                SecureScreenSettingsListItem(Modifier.fillMaxWidth())
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
        icon = {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = null
            )
        },
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun PersonalizeNutritionFactsSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsListItem(
        label = { Text(stringResource(Res.string.headline_nutrition_facts)) },
        onClick = onClick,
        modifier = modifier,
        supportingContent = {
            Text(stringResource(Res.string.description_personalize_nutrition_facts_short))
        },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.List,
                contentDescription = null
            )
        },
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun SecureScreenSettingsListItem(modifier: Modifier = Modifier) {
    val hideContent = userPreference<HideContent>()
    val checked by hideContent.collectAsStateWithLifecycle(hideContent.getBlocking())

    SettingsListItem(
        label = {
            Text(stringResource(Res.string.headline_secure_screen))
        },
        onClick = { hideContent.setBlocking(!checked) },
        modifier = modifier,
        supportingContent = {
            Text(stringResource(Res.string.action_prevent_screen_capture))
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null
            )
        },
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}
