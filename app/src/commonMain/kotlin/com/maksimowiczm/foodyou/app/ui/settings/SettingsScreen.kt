package com.maksimowiczm.foodyou.app.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.SettingsListItem
import com.maksimowiczm.foodyou.common.compose.extension.add
import com.maksimowiczm.foodyou.common.config.AppConfig
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSponsor: () -> Unit,
    onAbout: () -> Unit,
    onMeals: () -> Unit,
    onLanguage: () -> Unit,
    onGoals: () -> Unit,
    onPersonalization: () -> Unit,
    onDatabase: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val color = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    val shape = RectangleShape

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_settings)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            item {
                SponsorSettingsListItem(
                    onClick = onSponsor,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                AnimatedWavyLine(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp).height(16.dp),
                )
            }

            item {
                PersonalizationSettingsListItem(
                    onClick = onPersonalization,
                    shape = shape,
                    color = color,
                    contentColor = contentColor,
                )
            }

            item {
                MealSettingsListItem(
                    onClick = onMeals,
                    shape = shape,
                    color = color,
                    contentColor = contentColor,
                )
            }

            item {
                GoalsSettingsListItem(
                    onClick = onGoals,
                    shape = shape,
                    color = color,
                    contentColor = contentColor,
                )
            }

            item {
                DatabaseSettingsListItem(
                    onClick = onDatabase,
                    shape = shape,
                    color = color,
                    contentColor = contentColor,
                )
            }

            item {
                LanguageSettingsListItem(
                    onClick = onLanguage,
                    shape = shape,
                    color = color,
                    contentColor = contentColor,
                )
            }

            item {
                val uriHandle = LocalUriHandler.current
                val appConfig = koinInject<AppConfig>()
                SettingsListItem(
                    icon = { Icon(Icons.Outlined.PrivacyTip, null) },
                    label = { Text(stringResource(Res.string.headline_privacy_policy)) },
                    supportingContent = {
                        Text(stringResource(Res.string.description_privacy_policy))
                    },
                    onClick = { uriHandle.openUri(appConfig.privacyPolicyUri) },
                    shape = shape,
                    color = color,
                    contentColor = contentColor,
                )
            }

            item {
                AboutSettingsListItem(
                    onClick = onAbout,
                    shape = shape,
                    color = color,
                    contentColor = contentColor,
                )
            }
        }
    }
}
