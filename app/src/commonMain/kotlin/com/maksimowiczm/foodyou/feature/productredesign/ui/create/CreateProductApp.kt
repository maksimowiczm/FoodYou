package com.maksimowiczm.foodyou.feature.productredesign.ui.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.component.BackHandler
import com.maksimowiczm.foodyou.feature.productredesign.ui.ProductForm
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ktlint:compose:vm-forwarding-check")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateProductApp(
    onBack: () -> Unit,
    onProductCreate: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateProductViewModel = koinViewModel()
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    val navController = rememberNavController()
    val currentDestination by navController
        .currentBackStackEntryFlow
        .collectAsStateWithLifecycle(null)

    val handleBack = {
        val destination = currentDestination?.destination

        when {
            destination == null -> Unit
            destination.hasRoute<CreateProductForm>() == true -> when (state.isModified) {
                true -> showDiscardDialog = true
                false -> onBack()
            }

            destination.hasRoute<CreateOpenFoodFactsProduct>() == true ->
                navController.popBackStack<CreateOpenFoodFactsProduct>(inclusive = true)
        }
    }
    // TODO Replace it with WebView on android?
    val onOpenFoodFacts = run {
        val uriHandler = LocalUriHandler.current
        val openFoodFactsUrl = stringResource(Res.string.link_open_food_facts)

        remember(uriHandler, openFoodFactsUrl) { { uriHandler.openUri(openFoodFactsUrl) } }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(
        enabled = currentDestination?.destination?.hasRoute<CreateProductForm>() == true &&
            state.isModified
    ) {
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
                    Text(stringResource(Res.string.headline_create_product))
                },
                actions = {
                    AnimatedVisibility(
                        visible =
                        currentDestination?.destination?.hasRoute<CreateProductForm>() == true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TextButton(
                            onClick = remember(viewModel) { viewModel::onCreateProduct },
                            enabled = state.isValid
                        ) {
                            Text(stringResource(Res.string.action_create))
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        CreateProductNavHost(
            onOpenFoodFacts = onOpenFoodFacts,
            onProductCreate = onProductCreate,
            contentPadding = paddingValues,
            viewModel = viewModel,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            navController = navController
        )
    }
}

@Serializable
private data object CreateOpenFoodFactsProduct

@Serializable
private data object CreateProductForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProductNavHost(
    onOpenFoodFacts: () -> Unit,
    onProductCreate: (productId: Long) -> Unit,
    contentPadding: PaddingValues,
    viewModel: CreateProductViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // This is a hack to force recomposition of the form when the user downloads a product
    // from Open Food Facts and returns to the form with the product data. It will recreate
    // text fields with the new data.
    var formKey by rememberSaveable { mutableIntStateOf(0) }

    val onProductCreated by rememberUpdatedState(onProductCreate)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel.eventBus) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventBus.collect { event ->
                when (event) {
                    is ProductFormEvent.DownloadedProductSuccessfully -> {
                        navController.navigate(CreateProductForm) {
                            launchSingleTop = true

                            popUpTo<CreateProductForm> {
                                inclusive = false
                            }
                        }

                        formKey = formKey + 1_000_690_420 * 1_000_420_690
                    }

                    ProductFormEvent.CreatingProduct -> Unit
                    is ProductFormEvent.ProductCreated -> onProductCreated(event.id)
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = CreateProductForm,
        modifier = modifier
    ) {
        forwardBackwardComposable<CreateOpenFoodFactsProduct> {
            val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
            val error by viewModel.openFoodFactsError.collectAsStateWithLifecycle()
            val link by viewModel.openFoodFactsLink.collectAsStateWithLifecycle()

            DownloadOpenFoodFactsProduct(
                linkInput = link,
                onLinkChange = remember(viewModel) { viewModel::onDownloadLinkChange },
                isDownloading = isDownloading,
                error = error,
                animatedVisibilityScope = this,
                onSearch = onOpenFoodFacts,
                onDownload = remember(viewModel) { viewModel::onDownloadOpenFoodFacts },
                contentPadding = contentPadding
            )
        }
        forwardBackwardComposable<CreateProductForm> {
            val state by viewModel.formState.collectAsStateWithLifecycle()
            val openFoodFactsError by viewModel.openFoodFactsError.collectAsStateWithLifecycle()
            val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()

            key(formKey.toString()) {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp).imePadding(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FlowRow {
                            AssistChip(
                                onClick = {
                                    navController.navigate(CreateOpenFoodFactsProduct) {
                                        launchSingleTop = true
                                    }
                                },
                                leadingIcon = {
                                    when {
                                        isDownloading -> CircularProgressIndicator(
                                            modifier = Modifier.size(AssistChipDefaults.IconSize)
                                        )

                                        openFoodFactsError != null -> Icon(
                                            imageVector = Icons.Default.Error,
                                            modifier = Modifier.size(AssistChipDefaults.IconSize),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )

                                        else -> Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = null,
                                            modifier = Modifier.size(AssistChipDefaults.IconSize)
                                        )
                                    }
                                },
                                enabled = !isDownloading,
                                label = {
                                    Text(
                                        stringResource(
                                            Res.string.action_use_open_food_facts_product
                                        )
                                    )
                                }
                            )
                        }
                    }

                    item {
                        ProductForm(
                            state = state,
                            onNameChange = remember(viewModel) { viewModel::onNameChange },
                            onBrandChange = remember(viewModel) { viewModel::onBrandChange },
                            onBarcodeChange = remember(viewModel) { viewModel::onBarcodeChange },
                            onProteinsChange = remember(viewModel) { viewModel::onProteinsChange },
                            onCarbohydratesChange = remember(viewModel) {
                                viewModel::onCarbohydratesChange
                            },
                            onFatsChange = remember(viewModel) { viewModel::onFatsChange },
                            onSugarsChange = remember(viewModel) { viewModel::onSugarsChange },
                            onSaturatedFatsChange = remember(viewModel) {
                                viewModel::onSaturatedFatsChange
                            },
                            onSaltChange = remember(viewModel) { viewModel::onSaltChange },
                            onSodiumChange = remember(viewModel) { viewModel::onSodiumChange },
                            onFiberChange = remember(viewModel) { viewModel::onFiberChange },
                            onPackageWeightChange = remember(viewModel) {
                                viewModel::onPackageWeightChange
                            },
                            onServingWeightChange = remember(viewModel) {
                                viewModel::onServingWeightChange
                            },
                            enabled = !isDownloading
                        )
                    }
                }
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
            Text(stringResource(Res.string.question_discard_product))
        }
    )
}
