package com.maksimowiczm.foodyou.ui.settings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.maksimowiczm.foodyou.feature.about.AboutSettingsListItem
import com.maksimowiczm.foodyou.feature.goals.GoalsSettingsListItem
import com.maksimowiczm.foodyou.feature.language.LanguageSettingsListItem
import com.maksimowiczm.foodyou.feature.meal.MealsSettingsListItem
import com.maksimowiczm.foodyou.feature.security.SecureScreenSettingsListItem
import com.maksimowiczm.foodyou.ui.home.HomeSettingsListItem
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onHomeSettings: () -> Unit,
    onMealsSettings: () -> Unit,
    onGoalsSettings: () -> Unit,
    onAbout: () -> Unit,
    onLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_settings)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                HomeSettingsListItem(
                    onClick = onHomeSettings
                )
            }
            item {
                MealsSettingsListItem(
                    onClick = onMealsSettings
                )
            }
            item {
                GoalsSettingsListItem(
                    onClick = onGoalsSettings
                )
            }
            item {
                SecureScreenSettingsListItem()
            }
            item {
                LanguageSettingsListItem(
                    onClick = onLanguage
                )
            }
            item {
                AboutSettingsListItem(
                    onClick = onAbout
                )
            }
        }
    }
}
