package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
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
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_cancel
import foodyou.app.generated.resources.action_create
import foodyou.app.generated.resources.action_discard
import foodyou.app.generated.resources.headline_create_product
import foodyou.app.generated.resources.link_open_food_facts
import foodyou.app.generated.resources.question_discard_product
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ktlint:compose:vm-forwarding-check")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateProductApp(
    onBack: () -> Unit,
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
                            onClick = {
                                // TODO
                            },
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
    contentPadding: PaddingValues,
    viewModel: CreateProductViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
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
                    }
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

            DownloadOpenFoodFactsProduct(
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

            ProductForm(
                state = state,
                contentPadding = contentPadding,
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
                onUseOpenFoodFactsProduct = remember(navController) {
                    {
                        navController.navigate(CreateOpenFoodFactsProduct) {
                            launchSingleTop = true
                        }
                    }
                }
            )
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
