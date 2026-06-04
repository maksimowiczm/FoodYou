package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters
import foodyou.app.generated.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

@Immutable
@Serializable
internal sealed interface SearchCollection {
    @Composable fun Icon(selected: Boolean, modifier: Modifier = Modifier)

    @Composable fun stringResource(): String

    fun LazyListScope.suffixFilters(onUpdate: (SearchCollection) -> Unit) = Unit

    @Immutable
    @Serializable
    class Favorite : SearchCollection {
        @Composable
        override fun Icon(selected: Boolean, modifier: Modifier) {
            androidx.compose.material3.Icon(
                imageVector =
                    if (selected) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = modifier,
            )
        }

        @Composable
        override fun stringResource(): String =
            org.jetbrains.compose.resources.stringResource(Res.string.headline_favorite)
    }

    @Immutable
    @Serializable
    class UserFood : SearchCollection {
        @Composable
        override fun Icon(selected: Boolean, modifier: Modifier) {
            androidx.compose.material3.Icon(
                imageVector = if (selected) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = null,
                modifier = modifier,
            )
        }

        @Composable
        override fun stringResource(): String =
            org.jetbrains.compose.resources.stringResource(Res.string.headline_your_food)
    }

    @Immutable
    @Serializable
    class OpenFoodFacts : SearchCollection {
        @Composable
        override fun Icon(selected: Boolean, modifier: Modifier) {
            Image(
                painter = painterResource(Res.drawable.openfoodfacts_logo),
                contentDescription = null,
                modifier = modifier,
            )
        }

        @Composable
        override fun stringResource(): String =
            org.jetbrains.compose.resources.stringResource(Res.string.headline_open_food_facts)
    }

    @Immutable
    @Serializable
    data class FoodDataCentral(
        val dataTypes: Set<FoodDataCentralSearchParameters.DataType> = setOf()
    ) : SearchCollection {
        @Composable
        override fun Icon(selected: Boolean, modifier: Modifier) {
            Image(
                painter = painterResource(Res.drawable.usda_logo),
                contentDescription = null,
                modifier = modifier,
            )
        }

        @Composable
        override fun stringResource(): String =
            org.jetbrains.compose.resources.stringResource(Res.string.headline_fooddata_central)

        override fun LazyListScope.suffixFilters(onUpdate: (SearchCollection) -> Unit) {
            item {
                var expanded by rememberSaveable { mutableStateOf(false) }
                val entries = FoodDataCentralSearchParameters.DataType.entries

                Box {
                    DropdownMenuPopup(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuGroup(shapes = MenuDefaults.groupShape(0, entries.size)) {
                            entries.forEachIndexed { i, entry ->
                                val checked = dataTypes.contains(entry)

                                DropdownMenuItem(
                                    checked = checked,
                                    onCheckedChange = {
                                        if (checked) onUpdate(copy(dataTypes = dataTypes - entry))
                                        else onUpdate(copy(dataTypes = dataTypes + entry))
                                    },
                                    text = { Text(entry.filter) },
                                    shapes = MenuDefaults.itemShape(i, entries.size),
                                    checkedLeadingIcon = {
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                        )
                                    },
                                )
                                if (i != entries.lastIndex) {
                                    Spacer(Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                    // Don't localize here as FoodData Central is English only
                    FilterChip(
                        selected = dataTypes.isNotEmpty(),
                        onClick = { expanded = true },
                        label = {
                            if (dataTypes.isEmpty()) {
                                Text("Data type")
                            } else {
                                val text =
                                    remember(dataTypes) {
                                        buildString {
                                            append(dataTypes.first().filter)
                                            if (dataTypes.size > 1) {
                                                append("+${dataTypes.size - 1}")
                                            }
                                        }
                                    }
                                Text(text)
                            }
                        },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                    )
                }
            }
        }
    }
}
