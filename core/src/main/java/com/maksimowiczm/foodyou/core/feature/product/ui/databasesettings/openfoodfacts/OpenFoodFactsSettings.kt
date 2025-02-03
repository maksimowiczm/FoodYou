package com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.openfoodfacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.country.CountryFlag
import com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.country.CountryPickerDialog
import com.maksimowiczm.foodyou.core.feature.system.data.model.Country
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module

@Composable
fun OpenFoodFactsSettings(
    settings: OpenFoodFactsSettings,
    onToggle: (Boolean) -> Unit,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = settings is OpenFoodFactsSettings.Enabled

    Surface(
        modifier = modifier
    ) {
        Column {
            Text(
                text = stringResource(R.string.open_food_facts),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            OpenFoodFactsDescription(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.action_use_open_food_facts)
                    )
                },
                modifier = Modifier.clickable { onToggle(!enabled) },
                trailingContent = {
                    Switch(
                        checked = enabled,
                        onCheckedChange = onToggle
                    )
                }
            )

            if (settings is OpenFoodFactsSettings.Enabled) {
                OpenFoodFactsContent(
                    onCountrySelected = onCountrySelected,
                    settings = settings
                )
            }
        }
    }
}

@Composable
private fun OpenFoodFactsDescription(
    modifier: Modifier = Modifier
) {
    val linkColor = MaterialTheme.colorScheme.tertiary

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(stringResource(R.string.open_food_facts))
        }
        append(" " + stringResource(R.string.description_open_food_facts))
        withLink(
            LinkAnnotation.Url(
                url = stringResource(R.string.link_open_food_facts),
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = linkColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            )
        ) {
            append(" " + stringResource(R.string.action_read_more))
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = annotatedString,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info_24),
                    contentDescription = null
                )

                val disclaimer = buildAnnotatedString {
                    append(stringResource(R.string.open_food_facts_disclaimer))
                    withLink(
                        LinkAnnotation.Url(
                            url = stringResource(R.string.link_open_food_facts_terms_of_use),
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = linkColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        )
                    ) {
                        append(" " + stringResource(R.string.action_see_terms_of_use))
                    }
                }

                Text(
                    text = disclaimer,
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun OpenFoodFactsContent(
    onCountrySelected: (Country) -> Unit,
    settings: OpenFoodFactsSettings.Enabled,
    modifier: Modifier = Modifier,
    countryFlag: CountryFlag = koinInject()
) {
    var showCountryPicker by rememberSaveable { mutableStateOf(false) }

    if (showCountryPicker) {
        CountryPickerDialog(
            onDismissRequest = { showCountryPicker = false },
            availableCountries = settings.availableCountries,
            countryFlag = countryFlag,
            onCountrySelected = { country ->
                onCountrySelected(country)
                showCountryPicker = false
            }
        )
    }

    Column {
        ListItem(
            modifier = modifier
                .requiredHeightIn(min = 64.dp)
                .clickable { showCountryPicker = true },
            headlineContent = {
                Text(
                    text = stringResource(R.string.action_select_country)
                )
            },
            supportingContent = {
                Text(
                    text = settings.country.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingContent = {
                countryFlag(
                    country = settings.country,
                    modifier = Modifier.width(52.dp)
                )
            }
        )
    }
}

@Preview
@Composable
private fun OpenFoodFactsSettingsDisabledPreview() {
    FoodYouTheme {
        OpenFoodFactsSettings(
            settings = OpenFoodFactsSettings.Disabled,
            onToggle = {},
            onCountrySelected = {}
        )
    }
}

@Preview
@Composable
private fun OpenFoodFactsSettingsPreview() {
    val countryFlag = CountryFlag { c, modifier ->
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = c.code)
        }
    }

    KoinApplication(
        application = { modules(module { single { countryFlag } }) }
    ) {
        FoodYouTheme {
            OpenFoodFactsSettings(
                settings = OpenFoodFactsSettings.Enabled(
                    country = Country.Poland,
                    availableCountries = listOf(Country.Poland, Country.UnitedStates)
                ),
                onToggle = {},
                onCountrySelected = {}
            )
        }
    }
}
