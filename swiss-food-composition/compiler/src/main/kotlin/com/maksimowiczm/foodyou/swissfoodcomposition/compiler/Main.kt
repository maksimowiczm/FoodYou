package com.maksimowiczm.foodyou.swissfoodcomposition.compiler

import kotlin.system.exitProcess

private const val USAGE =
    "Usage: swiss-food-composition-compiler --data <file> --i18n <dir> --out <file>"

fun main(args: Array<String>) {
    Args.parse(args)
        .fold(
            ifLeft = { fatal(it, exitCode = 2) },
            ifRight = { parsed ->
                when (parsed) {
                    Args.Empty -> println(USAGE)
                    is Args.Input -> runPipeline(parsed)
                }
            },
        )
}

private fun runPipeline(input: Args.Input) {
    Pipeline(
            csvReader = CsvReader(),
            i18nReader = I18nReader(),
            entityBuilder = EntityBuilder(),
            databaseBuilder = DatabaseBuilder(),
        )
        .run(
            dataFile = input.dataFile,
            i18nDirectory = input.i18nDirectory,
            outputFile = input.outputFile,
        )
        .fold(
            ifLeft = { fatal(it, exitCode = 1) },
            ifRight = { exitProcess(0) },
        )
}

private fun fatal(errors: Errors, exitCode: Int): Nothing {
    errors.forEach { System.err.println("fatal: $it") }
    exitProcess(exitCode)
}
