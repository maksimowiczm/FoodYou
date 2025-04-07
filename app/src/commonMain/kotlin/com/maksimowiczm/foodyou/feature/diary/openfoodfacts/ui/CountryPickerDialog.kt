package com.maksimowiczm.foodyou.feature.diary.openfoodfacts.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.maksimowiczm.foodyou.core.data.Country
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
internal fun CountryPickerDialog(
    availableCountries: List<Country>,
    onCountrySelect: (Country) -> Unit,
    onDismissRequest: () -> Unit,
    headline: @Composable () -> Unit = {
        Text(
            text = stringResource(Res.string.action_select_country),
            style = MaterialTheme.typography.titleLarge
        )
    },
    textFieldState: TextFieldState = rememberTextFieldState()
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = Modifier
                .height(600.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    headline()
                }

                Spacer(Modifier.height(8.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .padding(horizontal = 16.dp),
                    state = textFieldState,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    lineLimits = TextFieldLineLimits.SingleLine
                )
                LazyColumn {
                    val search = textFieldState.text

                    items(
                        items = availableCountries
                            .filter {
                                it.name.contains(search, true) ||
                                    it.code.contains(
                                        search,
                                        true
                                    )
                            },
                        key = { it.code }
                    ) { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCountrySelect(country) }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = country.name
                            )

                            CountryFlag(
                                country = country,
                                modifier = Modifier.width(52.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
