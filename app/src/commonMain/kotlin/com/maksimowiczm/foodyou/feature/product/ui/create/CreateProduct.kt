package com.maksimowiczm.foodyou.feature.product.ui.create

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.ui.component.BackHandler
import com.maksimowiczm.foodyou.core.ui.ext.plus
import com.maksimowiczm.foodyou.feature.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.product.ui.ProductForm
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ktlint:compose:vm-forwarding-check")
@Composable
internal fun CreateProduct(
    onClose: () -> Unit,
    onCreate: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateProductViewModel = koinViewModel()
) {
    val navController = rememberNavController()

    val lifecycleOwner = LocalLifecycleOwner.current
    val onCreate by rememberUpdatedState(onCreate)
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.idState.collect { state ->
                when (state) {
                    is CreateState.Created -> onCreate(state.productId)
                    CreateState.CreatingProduct,
                    CreateState.Nothing -> Unit
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "form",
        modifier = modifier
    ) {
        crossfadeComposable("form") {
            CreateProduct(
                onClose = onClose,
                onBarcodeScanner = {
                    navController.navigate("barcode") {
                        launchSingleTop = true
                    }
                },
                viewModel = viewModel
            )
        }
        crossfadeComposable("barcode") {
            CameraBarcodeScannerScreen(
                onClose = {
                    navController.popBackStack("barcode", true)
                },
                onBarcodeScan = {
                    viewModel.onBarcodeChange(it)
                    navController.popBackStack("barcode", true)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProduct(
    onClose: () -> Unit,
    onBarcodeScanner: () -> Unit,
    viewModel: CreateProductViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = {
                showDiscardDialog = false
                onClose()
            }
        )
    }

    val handleClose = {
        if (state.isModified) {
            showDiscardDialog = true
        } else {
            onClose()
        }
    }

    BackHandler(
        enabled = state.isModified
    ) {
        showDiscardDialog = true
    }

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = handleClose
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.action_close)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.headline_create_product)
                    )
                },
                actions = {
                    TextButton(
                        onClick = remember(viewModel) { viewModel::onCreate },
                        enabled = state.isValid
                    ) {
                        Text(
                            text = stringResource(Res.string.action_create)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        ProductForm(
            contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
            state = state,
            onNameChange = remember(viewModel) { viewModel::onNameChange },
            onBrandChange = remember(viewModel) { viewModel::onBrandChange },
            onBarcodeChange = remember(viewModel) { viewModel::onBarcodeChange },
            onBarcodeScanner = onBarcodeScanner,
            onProteinsChange = remember(viewModel) { viewModel::onProteinsChange },
            onCarbohydratesChange = remember(viewModel) { viewModel::onCarbohydratesChange },
            onFatsChange = remember(viewModel) { viewModel::onFatsChange },
            onSugarsChange = remember(viewModel) { viewModel::onSugarsChange },
            onSaturatedFatsChange = remember(viewModel) { viewModel::onSaturatedFatsChange },
            onSaltChange = remember(viewModel) { viewModel::onSaltChange },
            onSodiumChange = remember(viewModel) { viewModel::onSodiumChange },
            onFiberChange = remember(viewModel) { viewModel::onFiberChange },
            onPackageWeightChange = remember(viewModel) { viewModel::onPackageWeightChange },
            onServingWeightChange = remember(viewModel) { viewModel::onServingWeightChange },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@Composable
private fun DiscardDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.action_discard))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = {
            Text(stringResource(Res.string.question_discard_product))
        }
    )
}
