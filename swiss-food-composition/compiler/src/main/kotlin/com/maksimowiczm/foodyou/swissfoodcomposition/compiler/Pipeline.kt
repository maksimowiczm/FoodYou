package com.maksimowiczm.foodyou.swissfoodcomposition.compiler

import arrow.core.raise.either
import java.io.File

class Pipeline(
    private val csvReader: CsvReader,
    private val i18nReader: I18nReader,
    private val entityBuilder: EntityBuilder,
    private val databaseBuilder: DatabaseBuilder,
) {
    fun run(dataFile: File, i18nDirectory: File, outputFile: File) = either {
        val csvContent = csvReader.readCsv(dataFile).bind()
        val translations = i18nReader.read(i18nDirectory).bind()
        val entities = entityBuilder.build(csvContent, translations).bind()
        databaseBuilder.build(outputFile, entities).bind()
    }
}
