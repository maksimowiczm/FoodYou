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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.maksimowiczm.foodyou.app.ui.common.component.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.app.ui.common.form.FormField2
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
internal fun ProductForm2(
    state: ProductForm2State,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        General(state = state, isLocked = isLocked)
    }
}

@Composable
private fun General(state: ProductForm2State, isLocked: Boolean, modifier: Modifier = Modifier) {
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
        )
        state.brand.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.product_brand)) },
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
        Spacer(Modifier.height(16.dp))
        state.packageQuantity.QuantityOutlinedTextField(
            label = stringResource(Res.string.product_package),
            quantity = state.packageUnit,
            isLocked = isLocked,
            isRequired = state.valuesPer.value == ValuesPer.Package,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        state.note.OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLocked,
            label = { Text(stringResource(Res.string.headline_note)) },
            supportingText = { Text(stringResource(Res.string.description_add_note)) },
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
private fun FormField2.QuantityOutlinedTextField(
    label: String,
    quantity: MutableState<QuantityUnit>,
    isLocked: Boolean,
    isRequired: Boolean,
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
            val error = error
            if (error != null) Text(error) else if (isRequired) Text(requiredStringResource())
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
                    val str =
                        when (quantity.value) {
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
        isError = error != null,
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
    )
}

@Composable
private fun FormField2.OutlinedTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable TextFieldLabelScope.() -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        enabled = enabled,
        label = label,
        trailingIcon = {
            if (trailingIcon != null) trailingIcon()
            else if (error != null) Icon(Icons.Outlined.Error, null)
        },
        supportingText = {
            val error = error
            if (error != null) Text(error) else supportingText?.invoke()
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
