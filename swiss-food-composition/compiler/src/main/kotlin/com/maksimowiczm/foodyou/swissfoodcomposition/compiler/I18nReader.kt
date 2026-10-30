package com.maksimowiczm.foodyou.swissfoodcomposition.compiler

import arrow.core.Either
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.maksimowiczm.foodyou.common.domain.Language
import java.io.File
import kotlinx.serialization.json.Json

typealias TranslationsMap = Map<Language, Map<Int, String>>

class I18nReader {
    fun read(directory: File): Either<Errors, TranslationsMap> = either {
        ensure(directory.exists()) {
            nonEmptyListOf("I18n directory does not exist: '${directory.path}'")
        }
        ensure(directory.isDirectory) {
            nonEmptyListOf("I18n path is not a directory: '${directory.path}'")
        }

        val jsonFiles =
            directory.listFiles { file -> file.extension.lowercase() == "json" }?.toList().orEmpty()
        ensure(jsonFiles.isNotEmpty()) {
            nonEmptyListOf("No JSON translation files found in directory: '${directory.path}'")
        }

        val translationsMap = mutableMapOf<Language, Map<Int, String>>()

        for (file in jsonFiles) {
            val langTag = file.nameWithoutExtension
            val language =
                ensureNotNull(Language.fromTag(langTag)) {
                    nonEmptyListOf(
                        "Unknown language for translation file '${file.name}': '$langTag'"
                    )
                }

            val jsonContent =
                Either.catch {
                        file.readText()
                    }
                    .mapLeft { throwable ->
                        nonEmptyListOf(
                            "Failed to read translation file '${file.path}': ${throwable.message ?: throwable.toString()}"
                        )
                    }
                    .bind()

            val rawMap =
                Either.catch {
                        Json.decodeFromString<Map<String, String>>(jsonContent)
                    }
                    .mapLeft { throwable ->
                        nonEmptyListOf(
                            "Failed to parse JSON translation file '${file.path}': ${throwable.message ?: throwable.toString()}"
                        )
                    }
                    .bind()

            val parsedTranslations = mutableMapOf<Int, String>()
            for ((key, value) in rawMap) {
                val id =
                    ensureNotNull(key.toIntOrNull()) {
                        nonEmptyListOf(
                            "Invalid food ID key '$key' in translation file '${file.name}'"
                        )
                    }
                parsedTranslations[id] = value
            }

            translationsMap[language] = parsedTranslations
        }

        translationsMap
    }
}
