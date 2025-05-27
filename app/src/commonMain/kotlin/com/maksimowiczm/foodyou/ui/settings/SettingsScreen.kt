package com.maksimowiczm.foodyou.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.feature.about.AboutSettingsListItem
import com.maksimowiczm.foodyou.feature.goals.GoalsSettingsListItem
import com.maksimowiczm.foodyou.feature.importexport.ImportExportSettingsListItem
import com.maksimowiczm.foodyou.feature.language.LanguageSettingsListItem
import com.maksimowiczm.foodyou.feature.meal.MealsSettingsListItem
import com.maksimowiczm.foodyou.feature.security.SecureScreenSettingsListItem
import com.maksimowiczm.foodyou.ui.home.HomeSettingsListItem
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onHomeSettings: () -> Unit,
    onMealsSettings: () -> Unit,
    onGoalsSettings: () -> Unit,
    onAbout: () -> Unit,
    onLanguage: () -> Unit,
    onImportExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val contentColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_settings)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            settingsItem {
                HomeSettingsListItem(
                    onClick = onHomeSettings,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
                MealsSettingsListItem(
                    onClick = onMealsSettings,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
                GoalsSettingsListItem(
                    onClick = onGoalsSettings,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }

            settingsItem {
                ImportExportSettingsListItem(
                    onClick = onImportExport,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }

            settingsItem {
                SecureScreenSettingsListItem(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
                LanguageSettingsListItem(
                    onClick = onLanguage,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }

            settingsItem {
                AboutSettingsListItem(
                    onClick = onAbout,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
        }
    }
}

private fun LazyListScope.settingsItem(content: @Composable ColumnScope.() -> Unit) {
    item {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(MaterialTheme.shapes.medium),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            content()
        }
    }
}
