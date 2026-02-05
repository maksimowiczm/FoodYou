package com.maksimowiczm.foodyou.app.ui.userfood.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.userfood.FillSuggestedFieldsDialog
import com.maksimowiczm.foodyou.app.ui.userfood.ProductForm
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditProductScreen(
    identity: UserFoodProductIdentity,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val viewModel: EditProductViewModel = koinViewModel { parametersOf(identity) }

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            EditProductEvent.Edited -> onEdit()
        }
    }

    val formState by viewModel.productFormState.collectAsStateWithLifecycle()
    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardChangesDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack) {
            Text(stringResource(Res.string.question_discard_product))
        }
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = !isLocked && formState.isModified,
        onBackCompleted = { showDiscardDialog = true },
    )
    val pleaseWaitStr = stringResource(Res.string.headline_please_wait)
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isLocked,
        onBackCompleted = {
            scope.launch { snackbarHostState.showSnackbar(message = pleaseWaitStr) }
        },
    )

    val focusRequester = remember { FocusRequester() }
    var showFillSuggestedFieldsDialog by rememberSaveable { mutableStateOf(false) }
    if (showFillSuggestedFieldsDialog) {
        FillSuggestedFieldsDialog(
            onDismissRequest = { showFillSuggestedFieldsDialog = false },
            onConfirm = {
                showFillSuggestedFieldsDialog = false
                focusRequester.requestFocus()
            },
            onSkip = {
                showFillSuggestedFieldsDialog = false
                viewModel.save()
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_edit_product)) },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = {
                            if (formState.isModified) showDiscardDialog = true else onBack()
                        },
                        enabled = !isLocked,
                    )
                },
                actions = {
                    val buttonHeight = ButtonDefaults.ExtraSmallContainerHeight
                    Button(
                        onClick = {
                            if (
                                formState.proteins.value == null ||
                                    formState.fats.value == null ||
                                    formState.carbohydrates.value == null ||
                                    formState.energy.value == null
                            ) {
                                showFillSuggestedFieldsDialog = true
                            } else {
                                viewModel.save()
                            }
                        },
                        shapes = ButtonDefaults.shapesFor(buttonHeight),
                        modifier =
                            Modifier.height(buttonHeight)
                                .then(if (isLocked) Modifier.shimmer() else Modifier),
                        enabled = formState.isValid && !isLocked,
                        contentPadding = ButtonDefaults.contentPaddingFor(buttonHeight),
                    ) {
                        Text(
                            text = stringResource(Res.string.action_save),
                            style = ButtonDefaults.textStyleFor(buttonHeight),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.imePadding()
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            item {
                ProductForm(
                    state = formState,
                    setImageUri = viewModel::setImage,
                    setValuesPer = viewModel::setValuesPer,
                    setServingUnit = viewModel::setServingUnit,
                    setPackageUnit = viewModel::setPackageUnit,
                    isLocked = isLocked,
                    macroFocusRequester = focusRequester,
                )
            }
        }
    }
}
