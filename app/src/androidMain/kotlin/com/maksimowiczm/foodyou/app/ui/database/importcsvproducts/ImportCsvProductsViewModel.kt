package com.maksimowiczm.foodyou.app.ui.database.importcsvproducts

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.csv.CsvParser
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.importexport.domain.entity.ProductField
import com.maksimowiczm.foodyou.importexport.domain.usecase.ImportCsvProductUseCase
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class ImportCsvProductsViewModel(
    private val importCsvProductUseCase: ImportCsvProductUseCase,
    private val csvParser: CsvParser,
    private val logger: Logger,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.WaitingForFile)
    val uiState = _uiState.asStateFlow()

    private var fileContent: ByteArray? = null

    fun handleCsv(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val stream = context.contentResolver.openInputStream(uri)
                if (stream == null) {
                    _uiState.value =
                        UiState.FailedToOpenFile(
                            "Failed to open file. Please ensure the file exists and is accessible."
                        )
                    return@launch
                }

                val content = stream.use { it.readBytes() }
                val header = csvParser.parse(content.toList().asFlow()).first()

                if (header.isEmpty() || header.any { it == null }) {
                    _uiState.value =
                        UiState.FailedToOpenFile("CSV file is empty or could not be read.")
                } else {
                    this@ImportCsvProductsViewModel.fileContent = content
                    _uiState.value = UiState.FileOpened(header.mapNotNull { it })
                }
            } catch (e: IOException) {
                _uiState.value = UiState.FailedToOpenFile("Error reading CSV file: ${e.message}")
            }
        }
    }

    fun import(fieldMap: Map<ProductField, String>) {
        val header = (_uiState.value as? UiState.FileOpened)?.header

        if (header == null) {
            _uiState.value = UiState.FailedToImport("No file opened. Please open a CSV file first.")
            return
        }

        if (requiredKeys.any { it !in fieldMap }) {
            _uiState.value =
                UiState.MissingRequiredFields(
                    "Missing required fields: ${requiredKeys.joinToString(", ")}. Please ensure all required fields are mapped."
                )
            return
        }

        // Read whole file content into memory. We are on mobile anyway, if it doesn't fit in memory
        // it won't be comfortable to use anyway. Might have to fix later 😆
        val fileContent = this.fileContent
        if (fileContent == null) {
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
                importCsvProducts(fileContent.toList().asFlow(), mapper)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                logger.w(TAG, e) { "Failed to import file" }
                _uiState.value = UiState.FailedToImport(e.message)
            } catch (e: Exception) {
                logger.w(TAG, e) { "Failed to import file" }
                _uiState.value = UiState.FailedToImport(e.message)
            } finally {
                this@ImportCsvProductsViewModel.fileContent = null
            }
        }
    }

    private suspend fun importCsvProducts(stream: Flow<Byte>, mapper: List<ProductField?>) {
        importCsvProductUseCase
            .import(
                mapper = mapper,
                stream = stream,
                source = FoodSource.Type.User,
                skipHeader = true,
            )
            .catch { throw it }
            .onEach { _uiState.value = UiState.Importing(it) }
            .last()
            .let { _uiState.value = UiState.ImportSuccess(it) }
    }

    private companion object {
        private const val TAG = "ImportCsvProductsViewModel"

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
