package com.maksimowiczm.foodyou.app.ui.userfood

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.component.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalNutrientsOrder
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.coil.securelyAccessFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.lastModified
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductForm(
    state: ProductFormState,
    isLocked: Boolean,
    macroFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val order = LocalNutrientsOrder.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        General(state = state, isLocked = isLocked)
        Macronutrients(state, isLocked, Modifier.focusRequester(macroFocusRequester))
        order.forEachIndexed { i, it ->
            val trailingImeAction = if (i == order.lastIndex) ImeAction.Done else ImeAction.Next
            when (it) {
                NutrientsOrder.Proteins -> Unit
                NutrientsOrder.Fats -> Fats(state, isLocked, trailingImeAction)
                NutrientsOrder.Carbohydrates -> Carbohydrates(state, isLocked, trailingImeAction)
                NutrientsOrder.Other -> Other(state, isLocked, trailingImeAction)
                NutrientsOrder.Vitamins -> Vitamins(state, isLocked, trailingImeAction)
                NutrientsOrder.Minerals -> Minerals(state, isLocked, trailingImeAction)
            }
        }
    }
}

@Composable
private fun General(state: ProductFormState, isLocked: Boolean, modifier: Modifier = Modifier) {
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }
    if (showBarcodeScanner) {
        FullScreenCameraBarcodeScanner(
            onClose = { showBarcodeScanner = false },
            onBarcodeScan = {
                state.barcode.textFieldState.setTextAndPlaceCursorAtEnd(it)
                showBarcodeScanner = false
            },
        )
    }

    Column(modifier) {
        state.name.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(requiredStringResource(stringResource(Res.string.product_name))) },
            supportingText = { Text(requiredStringResource()) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        state.brand.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.product_brand)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(Modifier.height(8.dp))
        state.barcode.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.product_barcode)) },
            trailingIcon = {
                FilledTonalIconButton(
                    onClick = { showBarcodeScanner = true },
                    shapes = IconButtonDefaults.shapes(),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_barcode_scanner),
                        contentDescription = null,
                    )
                }
            },
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        )
        Spacer(Modifier.height(16.dp))
        PhotoPicker(
            imageUri = state.imageUri.value,
            setImageUri = { state.imageUri.value = it },
            isLocked = isLocked,
            modifier = Modifier.fillMaxWidth().then(if (isLocked) Modifier.shimmer() else Modifier),
        )
        Spacer(Modifier.height(16.dp))
        ValuesPerPicker(
            selected = state.valuesPer.value,
            onSelect = { state.valuesPer.value = it },
            isLocked = isLocked,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        state.servingQuantity.QuantityOutlinedTextField(
            label = stringResource(Res.string.product_serving),
            quantity = state.servingUnit,
            isLocked = isLocked,
            isRequired = state.valuesPer.value == ValuesPer.Serving,
            modifier = Modifier.fillMaxWidth(),
        )
        state.packageQuantity.QuantityOutlinedTextField(
            label = stringResource(Res.string.product_package),
            quantity = state.packageUnit,
            isLocked = isLocked,
            isRequired = state.valuesPer.value == ValuesPer.Package,
            modifier = Modifier.fillMaxWidth(),
        )
        state.note.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.headline_note)) },
            supportingText = { Text(stringResource(Res.string.description_add_note)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
    }
}

@Composable
private fun PhotoPicker(
    imageUri: String?,
    setImageUri: (String?) -> Unit,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var isPicking by rememberSaveable { mutableStateOf(false) }

    if (imageUri == null) {
        Button(
            onClick = {
                scope.launch {
                    isPicking = true

                    val image = FileKit.openFilePicker(type = FileKitType.Image)
                    if (image != null) {
                        setImageUri(image.path)
                    }

                    isPicking = false
                }
            },
            shapes =
                ButtonDefaults.shapes(MaterialTheme.shapes.large, MaterialTheme.shapes.extraLarge),
            modifier =
                modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .then(if (isPicking) Modifier.shimmer() else Modifier),
            enabled = !isLocked,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
        ) {
            Icon(imageVector = Icons.Outlined.AddAPhoto, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.action_add_a_photo),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    } else {
        val file = remember(imageUri) { PlatformFile(imageUri) }
        val platformContext = LocalPlatformContext.current
        val model =
            remember(platformContext, imageUri) {
                val file = PlatformFile(imageUri)
                // This is to force Coil to reload the image when the file is modified, it kind of
                // leaks from infrastructure to UI layer
                val cacheKey = "${file.absolutePath()}_${file.lastModified()}"

                ImageRequest.Builder(platformContext)
                    .data(imageUri)
                    .memoryCacheKey(cacheKey)
                    .diskCacheKey(cacheKey)
                    .build()
            }

        Surface(
            modifier = modifier.fillMaxWidth().heightIn(min = 80.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Box {
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier =
                        Modifier.align(Alignment.Center)
                            .fillMaxSize()
                            .sizeIn(
                                maxWidth =
                                    LocalDensity.current.run {
                                        LocalWindowInfo.current.containerSize.width.toDp()
                                    },
                                maxHeight = 400.dp,
                            ),
                    onState = { it.securelyAccessFile(file) },
                )
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val pressStated by interactionSource.collectIsPressedAsState()
                    val corner by animateDpAsState(if (pressStated) 8.dp else 20.dp)
                    val color =
                        if (isLocked) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.surfaceContainer

                    Surface(
                        onClick = { setImageUri(null) },
                        shape = RoundedCornerShape(corner),
                        interactionSource = interactionSource,
                        shadowElevation = 1.dp,
                        enabled = !isLocked,
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        Box(Modifier.size(40.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription =
                                    stringResource(Res.string.action_remove_a_photo),
                                modifier = Modifier.align(Alignment.Center).size(24.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ValuesPerPicker(
    selected: ValuesPer,
    onSelect: (ValuesPer) -> Unit,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val possibleValues = remember {
        listOf(ValuesPer.Grams100, ValuesPer.Milliliters100, ValuesPer.Serving, ValuesPer.Package)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.headline_values_per),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(Res.string.description_values_per),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(16.dp))
        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = { expanded = !expanded },
                    enabled = !isLocked,
                ) {
                    Text(text = selected.stringResource())
                }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    checked = expanded,
                    onCheckedChange = { expanded = it },
                    enabled = !isLocked,
                ) {
                    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        modifier =
                            Modifier.size(SplitButtonDefaults.TrailingIconSize).graphicsLayer {
                                this.rotationZ = rotation
                            },
                        contentDescription = null,
                    )
                }

                // Had to put it here because it must be aligned to the trailing button
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    possibleValues.forEach {
                        DropdownMenuItem(
                            text = { Text(it.stringResource()) },
                            onClick = {
                                expanded = false
                                onSelect(it)
                            },
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun Macronutrients(
    state: ProductFormState,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_macronutrients),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        LocalNutrientsOrder.current.forEach {
            when (it) {
                NutrientsOrder.Proteins ->
                    state.proteins.OutlinedTextField(
                        label = { Text(stringResource(Res.string.nutriment_proteins)) },
                        modifier = Modifier.fillMaxWidth(),
                        suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                        enabled = !isLocked,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next,
                            ),
                    )

                NutrientsOrder.Fats ->
                    state.fats.OutlinedTextField(
                        label = { Text(stringResource(Res.string.nutriment_fats)) },
                        modifier = Modifier.fillMaxWidth(),
                        suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                        enabled = !isLocked,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next,
                            ),
                    )

                NutrientsOrder.Carbohydrates ->
                    state.carbohydrates.OutlinedTextField(
                        label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                        modifier = Modifier.fillMaxWidth(),
                        suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                        enabled = !isLocked,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next,
                            ),
                    )

                NutrientsOrder.Other,
                NutrientsOrder.Vitamins,
                NutrientsOrder.Minerals -> Unit
            }
        }
        state.energy.OutlinedTextField(
            label = { Text(stringResource(Res.string.unit_energy)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(LocalEnergyFormatter.current.suffix()) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
    }
}

@Composable
private fun Fats(
    state: ProductFormState,
    isLocked: Boolean,
    trailingImeAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.nutriment_fats),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.saturatedFats.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_saturated_fats)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.transFats.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_trans_fats)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.monounsaturatedFats.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_monounsaturated_fats)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.polyunsaturatedFats.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_polyunsaturated_fats)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.omega3.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_omega_3)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.omega6.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_omega_6)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = trailingImeAction),
        )
    }
}

@Composable
private fun Carbohydrates(
    state: ProductFormState,
    isLocked: Boolean,
    trailingImeAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.nutriment_carbohydrates),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.sugars.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_sugars)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.addedSugars.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_added_sugars)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.dietaryFiber.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_fiber)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.solubleFiber.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_soluble_fiber)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.insolubleFiber.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_insoluble_fiber)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = trailingImeAction),
        )
    }
}

@Composable
private fun Other(
    state: ProductFormState,
    isLocked: Boolean,
    trailingImeAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_other),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.salt.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_salt)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.cholesterolMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_cholesterol)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.caffeineMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.nutriment_caffeine)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = trailingImeAction),
        )
    }
}

@Composable
private fun Vitamins(
    state: ProductFormState,
    isLocked: Boolean,
    trailingImeAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_vitamins),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.vitaminAMicro.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_a)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminB1Milli.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_b1)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminB2Milli.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_b2)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminB3Milli.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_b3)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminB5Milli.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_b5)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminB6Milli.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_b6)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminB7Micro.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_b7)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminB9Micro.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_b9)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminB12Micro.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_b12)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminCMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_c)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminDMicro.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_d)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminEMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_e)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.vitaminKMicro.OutlinedTextField(
            label = { Text(stringResource(Res.string.vitamin_k)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = trailingImeAction),
        )
    }
}

@Composable
private fun Minerals(
    state: ProductFormState,
    isLocked: Boolean,
    trailingImeAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_minerals),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.manganeseMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_manganese)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.magnesiumMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_magnesium)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.potassiumMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_potassium)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.calciumMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_calcium)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.copperMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_copper)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.zincMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_zinc)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.sodiumMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_sodium)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.ironMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_iron)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.phosphorusMilli.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_phosphorus)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.seleniumMicro.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_selenium)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.iodineMicro.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_iodine)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )
        state.chromiumMicro.OutlinedTextField(
            label = { Text(stringResource(Res.string.mineral_chromium)) },
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            enabled = !isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = trailingImeAction),
        )
    }
}

@Composable
private fun FormField.QuantityOutlinedTextField(
    label: String,
    quantity: MutableState<QuantityUnit>,
    isLocked: Boolean,
    isRequired: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
) {
    val error = error
    var expanded by rememberSaveable { mutableStateOf(false) }

    val menu =
        @Composable {
            DropdownMenu(
                expanded = expanded && !isLocked,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_gram_short)) },
                    onClick = {
                        quantity.value = QuantityUnit.Gram
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_ounce_short)) },
                    onClick = {
                        quantity.value = QuantityUnit.Ounce
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_milliliter_short)) },
                    onClick = {
                        quantity.value = QuantityUnit.Milliliter
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_fluid_ounce_short)) },
                    onClick = {
                        quantity.value = QuantityUnit.FluidOunce
                        expanded = false
                    },
                )
            }
        }

    OutlinedTextField(
        state = textFieldState,
        modifier = modifier.fillMaxWidth(),
        enabled = !isLocked,
        label = {
            val str = if (isRequired) requiredStringResource(label) else label
            Text(str)
        },
        supportingText = {
            if (error != null) Text(error)
            else Spacer(Modifier.height(LocalTextStyle.current.toDp()))
        },
        trailingIcon = {
            menu()
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (error != null) {
                    Icon(imageVector = Icons.Outlined.Error, contentDescription = null)
                }
                FilledTonalIconButton(
                    onClick = { expanded = !expanded },
                    shapes = IconButtonDefaults.shapes(),
                    enabled = !isLocked,
                ) {
                    Text(quantity.value.stringResource())
                }
            }
        },
        isError = error != null,
        keyboardOptions = keyboardOptions,
    )
}

@Composable
private fun FormField.OutlinedTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable TextFieldLabelScope.() -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val error = error

    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        enabled = enabled,
        label = label,
        trailingIcon =
            trailingIcon
                ?: if (error != null) {
                    { Icon(Icons.Outlined.Error, null) }
                } else null,
        suffix = suffix,
        supportingText =
            if (error != null) {
                { Text(error) }
            } else if (supportingText != null) supportingText
            else {
                { Spacer(Modifier.height(LocalTextStyle.current.toDp())) }
            },
        isError = error != null,
        keyboardOptions = keyboardOptions,
    )
}

@Composable
private fun requiredStringResource(): String = buildString {
    append("* ")
    append(stringResource(Res.string.neutral_required))
}

@Composable
private fun requiredStringResource(str: String): String = buildString {
    append(str)
    append(" *")
}
