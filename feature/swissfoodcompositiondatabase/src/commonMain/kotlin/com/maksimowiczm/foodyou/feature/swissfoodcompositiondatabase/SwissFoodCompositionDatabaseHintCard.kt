package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SwissFoodCompositionDatabaseHintCard(onAdd: () -> Unit, modifier: Modifier = Modifier) {
    val dataStore = koinInject<DataStore<Preferences>>()

    val showHint = dataStore
        .observe(SwissFoodCompositionDatabasePreferences.showHint)
        .map { it ?: true }
        .collectAsStateWithLifecycle(
            dataStore.getBlocking(SwissFoodCompositionDatabasePreferences.showHint) ?: true
        )
        .value

    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = showHint,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier
    ) {
        SwissFoodCompositionDatabaseHintCard(
            onAdd = onAdd,
            onDontShowAgain = coroutineScope.lambda {
                dataStore.set(SwissFoodCompositionDatabasePreferences.showHint to false)
            }
        )
    }
}

@Composable
private fun SwissFoodCompositionDatabaseHintCard(
    onAdd: () -> Unit,
    onDontShowAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.headline_swiss_food_composition_database),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(Res.string.description_swiss_food_composition_database),
                style = MaterialTheme.typography.bodyMedium
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onDontShowAgain) {
                    Text(stringResource(Res.string.action_dont_show_again))
                }
                Button(onAdd) {
                    Text(stringResource(Res.string.action_add))
                }
            }
        }
    }
}
