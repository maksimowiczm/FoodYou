package com.maksimowiczm.foodyou.feature.product.ui.update

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Suppress("ktlint:compose:vm-forwarding-check")
@Composable
internal fun UpdateProduct(
    productId: Long,
    onClose: () -> Unit,
    onUpdate: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateProductViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )
) {
    val navController = rememberNavController()

    val lifecycleOwner = LocalLifecycleOwner.current
    val onUpdate by rememberUpdatedState(onUpdate)
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.idState.collect { state ->
                when (state) {
                    is UpdateState.Updated -> onUpdate(state.productId)
                    UpdateState.UpdatingProduct,
                    UpdateState.Nothing -> Unit
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
            UpdateProduct(
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
private fun UpdateProduct(
    onClose: () -> Unit,
    onBarcodeScanner: () -> Unit,
    viewModel: UpdateProductViewModel,
    modifier: Modifier = Modifier
) {
    val state = run {
        viewModel.state.collectAsStateWithLifecycle().value
    }

    if (state == null) {
        Surface { Spacer(Modifier.fillMaxSize()) }
        return
    }

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
                    Text(stringResource(Res.string.headline_edit_product))
                },
                actions = {
                    TextButton(
                        onClick = remember(viewModel) { viewModel::onUpdate },
                        enabled = state.isValid
                    ) {
                        Text(
                            text = stringResource(Res.string.action_save)
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
            Text(stringResource(Res.string.question_discard_changes))
        }
    )
}
