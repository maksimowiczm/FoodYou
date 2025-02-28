package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun OpenFoodFactsSearchHint(
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewmodel: OpenFoodFactsSearchHintViewModel = koinViewModel()
) {
    val show by viewmodel.showSearchHint.collectAsStateWithLifecycle()

    // Don't show hint if settings are already enabled
    if (!show) {
        return
    }

    OpenFoodFactsSearchHint(
        onGoToSettings = onGoToSettings,
        onDontShowAgain = viewmodel::onDontShowAgain,
        modifier = modifier
    )
}

@Composable
private fun OpenFoodFactsSearchHint(
    onGoToSettings: () -> Unit,
    onDontShowAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cloud_download_24),
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.headline_remote_food_database),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.description_open_food_facts_search_hint),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDontShowAgain
                    ) {
                        Text(
                            text = stringResource(R.string.action_don_t_show_again)
                        )
                    }
                    Button(
                        onClick = onGoToSettings
                    ) {
                        Text(
                            text = stringResource(R.string.action_enable)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun OpenFoodFactsSearchHintPreview() {
    FoodYouTheme {
        OpenFoodFactsSearchHint(
            onGoToSettings = {},
            onDontShowAgain = {}
        )
    }
}
