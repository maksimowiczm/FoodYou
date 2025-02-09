package com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun rememberSearchBarState(
    initialExpanded: Boolean = false,
    initialTextFieldState: TextFieldState = rememberTextFieldState(),
    initialRecentQueries: List<ProductQuery> = emptyList()
): SearchBarState = if (Build.VERSION.SDK_INT >= 28) {
    rememberSaveable(
        initialExpanded,
        initialTextFieldState,
        initialRecentQueries,
        saver = Saver(
            save = { state ->
                state.expanded
            },
            restore = {
                SearchBarStateImpl(
                    expanded = it,
                    textFieldState = initialTextFieldState,
                    recentQueries = initialRecentQueries
                )
            }
        )
    ) {
        SearchBarStateImpl(
            expanded = initialExpanded,
            textFieldState = initialTextFieldState,
            recentQueries = initialRecentQueries
        )
    }
} else {
    val coroutineScope = rememberCoroutineScope()

    rememberSaveable(
        initialExpanded,
        initialTextFieldState,
        initialRecentQueries,
        coroutineScope,
        saver = Saver(
            save = { state ->
                state.expanded
            },
            restore = {
                SearchBarStateBelow28(
                    expanded = it,
                    textFieldState = initialTextFieldState,
                    recentQueries = initialRecentQueries,
                    coroutineScope = coroutineScope
                )
            }
        )
    ) {
        SearchBarStateBelow28(
            expanded = initialExpanded,
            textFieldState = initialTextFieldState,
            recentQueries = initialRecentQueries,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
private class SearchBarStateBelow28(
    expanded: Boolean,
    override val textFieldState: TextFieldState,
    recentQueries: List<ProductQuery>,
    private val coroutineScope: CoroutineScope
) : SearchBarState {
    override var expanded: Boolean by mutableStateOf(expanded)
        private set

    override var recentQueries: List<ProductQuery> by mutableStateOf(recentQueries)
        private set

    override fun updateRecentQueries(queries: List<ProductQuery>) {
        recentQueries = queries
    }

    private var _expandJob: Job? = null

    override fun requestExpandedState(state: Boolean) {
        // Prevent expanding on collapse with API < 28
        if (_expandJob?.isActive == true && state) {
            return
        }

        expanded = state

        _expandJob?.cancel()
        // I think that there might be a race condition here but does it really matter? :)
        _expandJob = coroutineScope.launch {
            delay(400)
        }
    }
}

@RequiresApi(28)
@Stable
private class SearchBarStateImpl(
    expanded: Boolean,
    override val textFieldState: TextFieldState,
    recentQueries: List<ProductQuery>
) : SearchBarState {
    override var expanded: Boolean by mutableStateOf(expanded)
        private set

    override var recentQueries: List<ProductQuery> by mutableStateOf(recentQueries)
        private set

    override fun updateRecentQueries(queries: List<ProductQuery>) {
        recentQueries = queries
    }

    override fun requestExpandedState(state: Boolean) {
        expanded = state
    }
}
