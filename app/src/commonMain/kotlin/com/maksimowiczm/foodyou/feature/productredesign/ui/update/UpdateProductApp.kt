package com.maksimowiczm.foodyou.feature.productredesign.ui.update

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.feature.productredesign.ui.ProductForm
import com.maksimowiczm.foodyou.feature.productredesign.ui.ProductFormState
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateProductApp(
    productId: Long,
    onBack: () -> Unit,
    onProductUpdate: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateProductViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()

    val onProductUpdate by rememberUpdatedState(onProductUpdate)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventBus.collect {
                when (it) {
                    is ProductFormEvent.ProductUpdated -> onProductUpdate(it.id)
                    ProductFormEvent.UpdatingProduct -> Unit
                }
            }
        }
    }

    when (val state = state) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> UpdateProductApp(
            state = state,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateProductApp(
    state: ProductFormState,
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
    val handleBack = {
        onBack()
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
                    onServingWeightChange = onServingWeightChange
                )
            }
        }
    }
}
