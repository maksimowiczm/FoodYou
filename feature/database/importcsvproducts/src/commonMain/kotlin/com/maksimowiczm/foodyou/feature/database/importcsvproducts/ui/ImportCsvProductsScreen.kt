package com.maksimowiczm.foodyou.feature.database.importcsvproducts.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.business.opensource.domain.importexport.ProductField
import com.maksimowiczm.foodyou.app.ui.shared.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.shared.component.DiscardDialog
import com.maksimowiczm.foodyou.feature.food.shared.ui.res.stringResource
import com.maksimowiczm.foodyou.shared.compose.component.BackHandler
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun ImportCsvProductsScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImportCsvProductsScreen(
    onBack: () -> Unit,
    onImport: (Map<ProductField, String>) -> Unit,
    header: List<String>,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    var fieldsMap by rememberSaveable { mutableStateOf(mapOf<ProductField, String>()) }

    BackHandler(enabled = fieldsMap.isNotEmpty(), onBack = { showDiscardDialog = true })

    if (showDiscardDialog) {
        DiscardDialog(onDiscard = onBack, onDismissRequest = { showDiscardDialog = false }) {
            Text(stringResource(Res.string.question_discard_changes))
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.action_import_csv_food_products)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    FilledIconButton(
                        onClick = { onImport(fieldsMap) },
                        enabled = fieldsMap.keys.containsAll(required),
                    ) {
                        Icon(painterResource(Res.drawable.ic_database_upload), null)
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 16.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.description_import_csv_food_products_hint),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text =
                            stringResource(Res.string.description_import_csv_food_products_hint_2),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    order.forEach { field ->
                        ProductFieldSelector(
                            field = field,
                            selected = fieldsMap[field],
                            onFieldSelected = { selectedField, columnName ->
                                fieldsMap =
                                    if (columnName == null) {
                                        fieldsMap - selectedField
                                    } else {
                                        fieldsMap + (selectedField to columnName)
                                    }
                            },
                            header = header,
                            required = field in required,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

private val required =
    listOf(
        ProductField.Name,
        ProductField.Energy,
        ProductField.Proteins,
        ProductField.Carbohydrates,
        ProductField.Fats,
    )

private val order =
    listOf(
        ProductField.Name,
        ProductField.Brand,
        ProductField.Barcode,
        ProductField.Note,
        ProductField.SourceUrl,
        ProductField.PackageWeight,
        ProductField.ServingWeight,
        ProductField.Energy,
        ProductField.Proteins,
        ProductField.Carbohydrates,
        ProductField.Fats,
        ProductField.Sugars,
        ProductField.AddedSugars,
        ProductField.DietaryFiber,
        ProductField.SolubleFiber,
        ProductField.InsolubleFiber,
        ProductField.SaturatedFats,
        ProductField.TransFats,
        ProductField.MonounsaturatedFats,
        ProductField.PolyunsaturatedFats,
        ProductField.Omega3,
        ProductField.Omega6,
        ProductField.Salt,
        ProductField.Cholesterol,
        ProductField.Caffeine,
        ProductField.VitaminA,
        ProductField.VitaminB1,
        ProductField.VitaminB2,
        ProductField.VitaminB3,
        ProductField.VitaminB5,
        ProductField.VitaminB6,
        ProductField.VitaminB7,
        ProductField.VitaminB9,
        ProductField.VitaminB12,
        ProductField.VitaminC,
        ProductField.VitaminD,
        ProductField.VitaminE,
        ProductField.VitaminK,
        ProductField.Manganese,
        ProductField.Magnesium,
        ProductField.Potassium,
        ProductField.Calcium,
        ProductField.Copper,
        ProductField.Zinc,
        ProductField.Sodium,
        ProductField.Iron,
        ProductField.Phosphorus,
        ProductField.Selenium,
        ProductField.Iodine,
        ProductField.Chromium,
    )

@Composable
private fun ProductFieldSelector(
    field: ProductField,
    selected: String?,
    onFieldSelected: (ProductField, String?) -> Unit,
    header: List<String>,
    required: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val value = selected ?: stringResource(Res.string.neutral_no_column)
    var expanded by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions
            .filter { it is PressInteraction.Release }
            .collectLatest { expanded = !expanded }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier =
            modifier.clickable(
                indication = null,
                interactionSource = null,
                onClick = { expanded = !expanded },
            ),
        isError = required && selected == null,
        readOnly = true,
        label = { Text(field.stringResource()) },
        trailingIcon = {
            if (selected != null) {
                IconButton(onClick = { onFieldSelected(field, null) }) {
                    Icon(Icons.Outlined.Clear, null)
                }
            } else {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Outlined.ArrowDropDown, null)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    header.forEach { columnName ->
                        DropdownMenuItem(
                            text = { Text(columnName) },
                            onClick = {
                                expanded = false
                                onFieldSelected(field, columnName)
                            },
                        )
                    }
                }
            }
        },
        interactionSource = interactionSource,
    )
}
