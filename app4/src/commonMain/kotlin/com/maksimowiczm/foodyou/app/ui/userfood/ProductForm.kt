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
fun ProductForm(
    state: ProductFormState,
    isLocked: Boolean,
    setImageUri: (String?) -> Unit,
    setValuesPer: (ValuesPer) -> Unit,
    setServingUnit: (QuantityUnit) -> Unit,
    setPackageUnit: (QuantityUnit) -> Unit,
    macroFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val order = LocalNutrientsOrder.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        General(
            state = state,
            setImageUri = setImageUri,
            setValuesPer = setValuesPer,
            setServingUnit = setServingUnit,
            setPackageUnit = setPackageUnit,
            isLocked = isLocked,
        )
        Macronutrients(state, isLocked, Modifier.focusRequester(macroFocusRequester))
        order.forEachIndexed { i, it ->
            val lastKeyboardAction = if (i == order.lastIndex) ImeAction.Done else ImeAction.Next

            when (it) {
                NutrientsOrder.Proteins -> Unit
                NutrientsOrder.Fats -> Fats(state, isLocked, lastKeyboardAction)
                NutrientsOrder.Carbohydrates -> Carbohydrates(state, isLocked, lastKeyboardAction)
                NutrientsOrder.Other -> Other(state, isLocked, lastKeyboardAction)
                NutrientsOrder.Vitamins -> Vitamins(state, isLocked, lastKeyboardAction)
                NutrientsOrder.Minerals -> Minerals(state, isLocked, lastKeyboardAction)
            }
        }
    }
}

@Composable
private fun General(
    state: ProductFormState,
    setImageUri: (String?) -> Unit,
    setValuesPer: (ValuesPer) -> Unit,
    setServingUnit: (QuantityUnit) -> Unit,
    setPackageUnit: (QuantityUnit) -> Unit,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
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
        Text(
            text = stringResource(Res.string.headline_general),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        OutlinedTextField(
            state = state.name.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(requiredStringResource(stringResource(Res.string.product_name))) },
            supportingText = { Text(requiredStringResource()) },
            trailingIcon = {
                if (!state.name.isValid) {
                    Icon(imageVector = Icons.Outlined.Error, contentDescription = null)
                }
            },
            isError = !state.name.isValid,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OutlinedTextField(
            state = state.brand.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.product_brand)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            state = state.barcode.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.product_barcode)) },
            isError = !state.barcode.isValid,
            supportingText = {
                val error = state.barcode.error
                if (error != null) {
                    Text(error.stringResource())
                }
            },
            trailingIcon = {
                FilledTonalIconButton(
                    onClick = { showBarcodeScanner = true },
                    shapes = IconButtonDefaults.shapes(),
                    enabled = !isLocked,
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
            imageUri = state.imageUri,
            setImageUri = setImageUri,
            isLocked = isLocked,
            modifier = Modifier.fillMaxWidth().then(if (isLocked) Modifier.shimmer() else Modifier),
        )
        Spacer(Modifier.height(16.dp))
        ValuesPerPicker(
            selected = state.valuesPer,
            onSelect = setValuesPer,
            isLocked = isLocked,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        state.servingQuantity.QuantityTextField(
            label = { Text(stringResource(Res.string.product_serving)) },
            isLocked = isLocked,
            quantityUnit = state.servingUnit,
            setQuantityUnit = setServingUnit,
        )
        state.packageQuantity.QuantityTextField(
            label = { Text(stringResource(Res.string.product_package)) },
            isLocked = isLocked,
            quantityUnit = state.packageUnit,
            setQuantityUnit = setPackageUnit,
        )
        OutlinedTextField(
            state = state.note.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.headline_note)) },
            supportingText = { Text(stringResource(Res.string.description_add_note)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OutlinedTextField(
            state = state.source.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.headline_source)) },
            supportingText = { Text(stringResource(Res.string.description_food_source)) },
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
private fun FormField<Double?, FormFieldError>.QuantityTextField(
    label: @Composable TextFieldLabelScope.() -> Unit,
    isLocked: Boolean,
    quantityUnit: QuantityUnit,
    setQuantityUnit: (QuantityUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                        setQuantityUnit(QuantityUnit.Gram)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_ounce_short)) },
                    onClick = {
                        setQuantityUnit(QuantityUnit.Ounce)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_milliliter_short)) },
                    onClick = {
                        setQuantityUnit(QuantityUnit.Milliliter)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.unit_fluid_ounce_short)) },
                    onClick = {
                        setQuantityUnit(QuantityUnit.FluidOunce)
                        expanded = false
                    },
                )
            }
        }

    OutlinedTextField(
        state = textFieldState,
        modifier = modifier.fillMaxWidth(),
        enabled = !isLocked,
        label = label,
        supportingText = { error?.let { Text(it.stringResource()) } },
        trailingIcon = {
            menu()
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (!isValid) {
                    Icon(imageVector = Icons.Outlined.Error, contentDescription = null)
                }
                FilledTonalIconButton(
                    onClick = { expanded = !expanded },
                    shapes = IconButtonDefaults.shapes(),
                    enabled = !isLocked,
                ) {
                    val str =
                        when (quantityUnit) {
                            QuantityUnit.Gram -> stringResource(Res.string.unit_gram_short)
                            QuantityUnit.Ounce -> stringResource(Res.string.unit_ounce_short)
                            QuantityUnit.Milliliter ->
                                stringResource(Res.string.unit_milliliter_short)

                            QuantityUnit.FluidOunce ->
                                stringResource(Res.string.unit_fluid_ounce_short)
                        }
                    Text(str)
                }
            }
        },
        isError = !isValid,
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
    )
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
                    state.proteins.TextField(
                        label = { Text(stringResource(Res.string.nutriment_proteins)) },
                        suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                        isLocked = isLocked,
                    )

                NutrientsOrder.Fats ->
                    state.fats.TextField(
                        label = { Text(stringResource(Res.string.nutriment_fats)) },
                        suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                        isLocked = isLocked,
                    )

                NutrientsOrder.Carbohydrates ->
                    state.carbohydrates.TextField(
                        label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                        suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                        isLocked = isLocked,
                    )

                NutrientsOrder.Other,
                NutrientsOrder.Vitamins,
                NutrientsOrder.Minerals -> Unit
            }
        }
        state.energy.TextField(
            label = { Text(stringResource(Res.string.unit_energy)) },
            suffix = { Text(LocalEnergyFormatter.current.suffix()) },
            isLocked = isLocked,
        )
    }
}

@Composable
private fun Fats(
    state: ProductFormState,
    isLocked: Boolean,
    lastKeyboardAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.nutriment_fats),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.saturatedFats.TextField(
            label = { Text(stringResource(Res.string.nutriment_saturated_fats)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.transFats.TextField(
            label = { Text(stringResource(Res.string.nutriment_trans_fats)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.monounsaturatedFats.TextField(
            label = { Text(stringResource(Res.string.nutriment_monounsaturated_fats)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.polyunsaturatedFats.TextField(
            label = { Text(stringResource(Res.string.nutriment_polyunsaturated_fats)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.omega3.TextField(
            label = { Text(stringResource(Res.string.nutriment_omega_3)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.omega6.TextField(
            label = { Text(stringResource(Res.string.nutriment_omega_6)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = lastKeyboardAction),
        )
    }
}

@Composable
private fun Carbohydrates(
    state: ProductFormState,
    isLocked: Boolean,
    lastKeyboardAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.nutriment_carbohydrates),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.sugars.TextField(
            label = { Text(stringResource(Res.string.nutriment_sugars)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.addedSugars.TextField(
            label = { Text(stringResource(Res.string.nutriment_added_sugars)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.dietaryFiber.TextField(
            label = { Text(stringResource(Res.string.nutriment_fiber)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.solubleFiber.TextField(
            label = { Text(stringResource(Res.string.nutriment_soluble_fiber)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.insolubleFiber.TextField(
            label = { Text(stringResource(Res.string.nutriment_insoluble_fiber)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = lastKeyboardAction),
        )
    }
}

@Composable
private fun Other(
    state: ProductFormState,
    isLocked: Boolean,
    lastKeyboardAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_other),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.salt.TextField(
            label = { Text(stringResource(Res.string.nutriment_salt)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isLocked = isLocked,
        )
        state.cholesterolMilli.TextField(
            label = { Text(stringResource(Res.string.nutriment_cholesterol)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.caffeineMilli.TextField(
            label = { Text(stringResource(Res.string.nutriment_caffeine)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = lastKeyboardAction),
        )
    }
}

@Composable
private fun Vitamins(
    state: ProductFormState,
    isLocked: Boolean,
    lastKeyboardAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_vitamins),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.vitaminAMicro.TextField(
            label = { Text(stringResource(Res.string.vitamin_a)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
        )
        state.vitaminB1Milli.TextField(
            label = { Text(stringResource(Res.string.vitamin_b1)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.vitaminB2Milli.TextField(
            label = { Text(stringResource(Res.string.vitamin_b2)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.vitaminB3Milli.TextField(
            label = { Text(stringResource(Res.string.vitamin_b3)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.vitaminB5Milli.TextField(
            label = { Text(stringResource(Res.string.vitamin_b5)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.vitaminB6Milli.TextField(
            label = { Text(stringResource(Res.string.vitamin_b6)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.vitaminB7Micro.TextField(
            label = { Text(stringResource(Res.string.vitamin_b7)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
        )
        state.vitaminB9Micro.TextField(
            label = { Text(stringResource(Res.string.vitamin_b9)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
        )
        state.vitaminB12Micro.TextField(
            label = { Text(stringResource(Res.string.vitamin_b12)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
        )
        state.vitaminCMilli.TextField(
            label = { Text(stringResource(Res.string.vitamin_c)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.vitaminDMicro.TextField(
            label = { Text(stringResource(Res.string.vitamin_d)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
        )
        state.vitaminEMilli.TextField(
            label = { Text(stringResource(Res.string.vitamin_e)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.vitaminKMicro.TextField(
            label = { Text(stringResource(Res.string.vitamin_k)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = lastKeyboardAction),
        )
    }
}

@Composable
private fun Minerals(
    state: ProductFormState,
    isLocked: Boolean,
    lastKeyboardAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.headline_minerals),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        state.manganeseMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_manganese)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.magnesiumMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_magnesium)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.potassiumMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_potassium)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.calciumMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_calcium)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.copperMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_copper)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.zincMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_zinc)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.sodiumMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_sodium)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.ironMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_iron)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.phosphorusMilli.TextField(
            label = { Text(stringResource(Res.string.mineral_phosphorus)) },
            suffix = { Text(stringResource(Res.string.unit_milligram_short)) },
            isLocked = isLocked,
        )
        state.seleniumMicro.TextField(
            label = { Text(stringResource(Res.string.mineral_selenium)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
        )
        state.iodineMicro.TextField(
            label = { Text(stringResource(Res.string.mineral_iodine)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
        )
        state.chromiumMicro.TextField(
            label = { Text(stringResource(Res.string.mineral_chromium)) },
            suffix = { Text(stringResource(Res.string.unit_microgram_short)) },
            isLocked = isLocked,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = lastKeyboardAction),
        )
    }
}

@Composable
private fun FormField<Double?, FormFieldError>.TextField(
    label: @Composable TextFieldLabelScope.() -> Unit,
    suffix: @Composable () -> Unit,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
    supportingText: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier.fillMaxWidth(),
        enabled = !isLocked,
        label = label,
        supportingText = {
            val error = this.error
            if (error != null) {
                Text(error.stringResource())
            } else if (supportingText != null) {
                supportingText()
            }
        },
        suffix = suffix,
        trailingIcon =
            if (!isValid) {
                { Icon(imageVector = Icons.Outlined.Error, contentDescription = null) }
            } else {
                null
            },
        isError = !isValid,
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
