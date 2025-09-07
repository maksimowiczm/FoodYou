package com.maksimowiczm.foodyou.feature.database.importcsvproducts.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.domain.ImportCsvProductUseCase
import com.maksimowiczm.foodyou.business.food.domain.ProductField
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import java.io.BufferedReader
import java.io.IOException
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.getValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.stream.consumeAsFlow

internal class ImportCsvProductsViewModel(
    private val importCsvProductUseCase: ImportCsvProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.WaitingForFile)
    val uiState = _uiState.asStateFlow()

    private var bufferedReader: BufferedReader? = null

    fun handleCsv(uri: Uri, context: Context) {
        val stream = context.contentResolver.openInputStream(uri)

        if (stream == null) {
            _uiState.value =
                UiState.FailedToOpenFile(
                    "Failed to open file. Please ensure the file exists and is accessible."
                )
            return
        }

        val bufferedReader = stream.bufferedReader()

        try {
            val line: String? = bufferedReader.readLine()

            if (line == null) {
                bufferedReader.close()
                _uiState.value = UiState.FailedToOpenFile("CSV file is empty or could not be read.")
                return
            }

            val header = line.split(",")
            _uiState.value = UiState.FileOpened(header)
        } catch (e: IOException) {
            bufferedReader.close()
            FoodYouLogger.e(TAG, e) { "Error reading CSV file." }
            _uiState.value = UiState.FailedToOpenFile("Error reading CSV file: ${e.message}")
        } finally {
            this.bufferedReader = bufferedReader
            addCloseable(bufferedReader)
        }
    }

    fun import(fieldMap: Map<ProductField, String>) {
        val header = (_uiState.value as? UiState.FileOpened)?.header

        if (header == null) {
            FoodYouLogger.e(TAG) { "No file opened, cannot import products." }
            _uiState.value = UiState.FailedToImport("No file opened. Please open a CSV file first.")
            return
        }

        if (requiredKeys.any { it !in fieldMap }) {
            FoodYouLogger.e(TAG) { "Missing required fields in the field map: $fieldMap" }
            _uiState.value =
                UiState.MissingRequiredFields(
                    "Missing required fields: ${requiredKeys.joinToString(", ")}. Please ensure all required fields are mapped."
                )
            return
        }

        val bufferedReader = this.bufferedReader
        if (bufferedReader == null) {
            FoodYouLogger.e(TAG) { "Stream is null, cannot import products." }
            _uiState.value = UiState.FailedToImport("Stream is null. Please open a CSV file first.")
            return
        }

        _uiState.value = UiState.Importing(0)

        val mapper =
            header.map { columnName ->
                fieldMap.firstNotNullOfOrNull { (field, value) ->
                    if (value == columnName) field else null
                }
            }

        viewModelScope.launch {
            try {
                importCsvProducts(bufferedReader, mapper)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                FoodYouLogger.e(TAG, e) { "Error reading CSV file." }
                _uiState.value = UiState.FailedToImport(e.message)
            } catch (e: Exception) {
                FoodYouLogger.e(TAG, e) { "Unexpected error during CSV import." }
                _uiState.value = UiState.FailedToImport(e.message)
            } finally {
                bufferedReader.close()
                this@ImportCsvProductsViewModel.bufferedReader = null
            }
        }
    }

    private suspend fun importCsvProducts(
        bufferedReader: BufferedReader,
        mapper: List<ProductField?>,
    ) {
        val lines = bufferedReader.lines().consumeAsFlow()

        importCsvProductUseCase
            .import(mapper, lines, FoodSource.Type.User)
            .catch { throw it }
            .onEach { _uiState.value = UiState.Importing(it) }
            .last()
            .let { _uiState.value = UiState.ImportSuccess(it) }
    }

    private companion object {
        const val TAG = "ImportCsvProductsViewModel"

        private val requiredKeys by lazy {
            setOf(
                ProductField.Name,
                ProductField.Energy,
                ProductField.Proteins,
                ProductField.Carbohydrates,
                ProductField.Fats,
            )
        }
    }
}
