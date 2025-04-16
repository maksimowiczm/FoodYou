package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ProductForm(
    animatedVisibilityScope: AnimatedVisibilityScope,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current

    var fabHeight by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier.animateFloatingActionButton(
                    visible = !animatedVisibilityScope.transition.isRunning,
                    alignment = Alignment.BottomEnd
                ).onSizeChanged {
                    fabHeight = it.height
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(Res.string.action_create)
                )
            }
        }
    ) {
        FlowRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection)
                )
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(contentPadding.calculateTopPadding()).fillMaxWidth())

            Text(
                text = stringResource(Res.string.headline_general),
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.product_name)) },
                supportingText = { RequiredLabel() },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.product_brand)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.product_barcode)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            Text(
                text = stringResource(Res.string.headline_macronutrients),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_proteins)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                supportingText = { RequiredLabel() },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                supportingText = { RequiredLabel() },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_fats)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                supportingText = { RequiredLabel() },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.unit_calories)) },
                supportingText = {
                    Text(stringResource(Res.string.neutral_calories_are_calculated))
                },
                suffix = { Text(stringResource(Res.string.unit_kcal)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                readOnly = true
            )

            Spacer(Modifier.height(contentPadding.calculateBottomPadding()).fillMaxWidth())
            val height = LocalDensity.current.run { fabHeight.toDp() }
            Spacer(Modifier.height(height).fillMaxWidth())
        }
    }
}

@Composable
private fun RequiredLabel(modifier: Modifier = Modifier) {
    Text(
        text = "* " + stringResource(Res.string.neutral_required),
        modifier = modifier
    )
}
