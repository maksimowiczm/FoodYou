package com.maksimowiczm.foodyou.feature.database.exportcsvproducts.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.ExportCsvProductsUseCase
import com.maksimowiczm.foodyou.business.food.domain.ProductField
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import java.io.BufferedWriter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class ExportProductsViewModel(
    private val exportCsvProductsUseCase: ExportCsvProductsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.WaitingForFile)
    val uiState = _uiState.asStateFlow()

    fun handleCsv(uri: Uri, context: Context) {
        val stream = context.contentResolver.openOutputStream(uri)

        if (stream == null) {
            _uiState.value =
                UiState.Error(
                    "Failed to open file. Please ensure the file exists and is accessible."
                )
            return
        }

        addCloseable(stream)

        viewModelScope.launch {
            try {
                stream.bufferedWriter().use { handleWriter(it) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                FoodYouLogger.e(TAG, e) { "Error exporting products to CSV." }
                _uiState.value = UiState.Error(e.message)
            }
        }
    }

    private suspend fun handleWriter(writer: BufferedWriter) {
        flow {
                val lines = exportCsvProductsUseCase.export(ProductField.entries)
                var count = 0
                lines.collect { line ->
                    writer.appendLine(line)
                    emit(count++)
                }
                writer.flush()
            }
            .catch { throw it }
            .conflate()
            .onEach { _uiState.value = UiState.Exporting(it) }
            .last()
            .let { _uiState.value = UiState.Exported(it) }
    }

    private companion object {
        const val TAG = "ExportProductsViewModel"
    }
}
