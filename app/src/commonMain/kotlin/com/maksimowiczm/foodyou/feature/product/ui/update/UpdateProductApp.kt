package com.maksimowiczm.foodyou.feature.product.ui.update

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.maksimowiczm.foodyou.feature.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.product.ui.ProductForm
import com.maksimowiczm.foodyou.feature.product.ui.ProductFormState
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateProductApp(
    productId: Long,
    onBack: () -> Unit,
    onProductUpdate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateProductViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()
    var enabled by remember { mutableStateOf(true) }

    val onProductUpdate by rememberUpdatedState(onProductUpdate)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventBus.collect {
                when (it) {
                    is ProductFormEvent.ProductUpdated -> onProductUpdate()
                    ProductFormEvent.UpdatingProduct -> enabled = false
                }
            }
        }
    }

    when (val state = state) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> UpdateProductNavHost(
            state = state,
            enabled = enabled,
            onNameChange = remember(viewModel) { viewModel::onNameChange },
            onBrandChange = remember(viewModel) { viewModel::onBrandChange },
            onBarcodeChange = remember(viewModel) { viewModel::onBarcodeChange },
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
            onBack = onBack,
            onSave = remember(viewModel) { viewModel::onUpdate },
            modifier = modifier
        )
    }
}

@Composable
private fun UpdateProductNavHost(
    state: ProductFormState,
    enabled: Boolean,
    onNameChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onProteinsChange: (String) -> Unit,
    onCarbohydratesChange: (String) -> Unit,
    onFatsChange: (String) -> Unit,
    onSugarsChange: (String) -> Unit,
    onSaturatedFatsChange: (String) -> Unit,
    onSaltChange: (String) -> Unit,
    onSodiumChange: (String) -> Unit,
    onFiberChange: (String) -> Unit,
    onPackageWeightChange: (String) -> Unit,
    onServingWeightChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Barcode scanner must be on same navController as the app because it should be fullscreen
    NavHost(
        navController = navController,
        startDestination = "app"
    ) {
        crossfadeComposable("app") {
            UpdateProductApp(
                state = state,
                enabled = enabled,
                onNameChange = onNameChange,
                onBrandChange = onBrandChange,
                onBarcodeChange = onBarcodeChange,
                onProteinsChange = onProteinsChange,
                onCarbohydratesChange = onCarbohydratesChange,
                onFatsChange = onFatsChange,
                onSugarsChange = onSugarsChange,
                onSaturatedFatsChange = onSaturatedFatsChange,
                onSaltChange = onSaltChange,
                onSodiumChange = onSodiumChange,
                onFiberChange = onFiberChange,
                onPackageWeightChange = onPackageWeightChange,
                onServingWeightChange = onServingWeightChange,
                onBack = onBack,
                onSave = onSave,
                onBarcodeScanner = {
                    navController.navigate("barcode") {
                        launchSingleTop = true
                    }
                },
                modifier = modifier
            )
        }
        crossfadeComposable("barcode") {
            CameraBarcodeScannerScreen(
                onClose = {
                    navController.popBackStack("barcode", inclusive = true)
                },
                onBarcodeScan = {
                    onBarcodeChange(it)
                    navController.popBackStack("barcode", inclusive = true)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateProductApp(
    state: ProductFormState,
    enabled: Boolean,
    onNameChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onProteinsChange: (String) -> Unit,
    onCarbohydratesChange: (String) -> Unit,
    onFatsChange: (String) -> Unit,
    onSugarsChange: (String) -> Unit,
    onSaturatedFatsChange: (String) -> Unit,
    onSaltChange: (String) -> Unit,
    onSodiumChange: (String) -> Unit,
    onFiberChange: (String) -> Unit,
    onPackageWeightChange: (String) -> Unit,
    onServingWeightChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    val handleBack = {
        if (state.isModified) {
            showDiscardDialog = true
        } else {
            onBack()
        }
    }

    BackHandler(enabled = state.isModified) {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = {
                showDiscardDialog = false
                onBack()
            }
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = handleBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(stringResource(Res.string.headline_edit_product))
                },
                actions = {
                    TextButton(
                        enabled = state.isValid,
                        onClick = onSave
                    ) {
                        Text(stringResource(Res.string.action_save))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                ProductForm(
                    state = state,
                    onNameChange = onNameChange,
                    onBrandChange = onBrandChange,
                    onBarcodeChange = onBarcodeChange,
                    onProteinsChange = onProteinsChange,
                    onCarbohydratesChange = onCarbohydratesChange,
                    onFatsChange = onFatsChange,
                    onSugarsChange = onSugarsChange,
                    onSaturatedFatsChange = onSaturatedFatsChange,
                    onSaltChange = onSaltChange,
                    onSodiumChange = onSodiumChange,
                    onFiberChange = onFiberChange,
                    onPackageWeightChange = onPackageWeightChange,
                    onServingWeightChange = onServingWeightChange,
                    onBarcodeScanner = onBarcodeScanner,
                    enabled = enabled
                )
            }
        }
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
