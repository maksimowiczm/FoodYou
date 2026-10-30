package com.maksimowiczm.foodyou.swissfoodcomposition.compiler

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.raise.zipOrAccumulate
import arrow.core.right
import java.io.File

sealed interface Args {
    data object Empty : Args

    data class Input(val dataFile: File, val i18nDirectory: File, val outputFile: File) : Args

    companion object {
        fun parse(args: Array<String>): Either<NonEmptyList<String>, Args> =
            if (args.isEmpty()) Empty.right() else parseInput(scan(args.toList()))
    }
}

private const val DATA = "--data"
private const val I18N = "--i18n"
private const val OUT = "--out"

private val KNOWN_FLAGS = setOf(DATA, I18N, OUT)

/** Raw tokens. A `null` value means the flag was given without one. */
private class Scan(val flags: Map<String, List<String?>>, val unknown: List<String>)

private fun scan(args: List<String>): Scan {
    val flags = mutableMapOf<String, MutableList<String?>>()
    val unknown = mutableListOf<String>()

    var i = 0
    while (i < args.size) {
        val arg = args[i++]
        if (arg !in KNOWN_FLAGS) {
            unknown += arg
            continue
        }
        val next = args.getOrNull(i)
        val value =
            if (next != null && !next.startsWith("--")) {
                i++
                next
            } else null
        flags.getOrPut(arg) { mutableListOf() } += value
    }

    return Scan(flags, unknown)
}

private fun parseInput(scan: Scan) = either {
    zipOrAccumulate(
        {
            val file = scan.file(DATA).bind()
            ensure(file.exists()) { "$DATA file does not exist: $file" }
            ensure(file.isFile) { "$DATA is not a file: $file" }
            file
        },
        {
            val dir = scan.file(I18N).bind()
            ensure(dir.exists()) { "$I18N directory does not exist: $dir" }
            ensure(dir.isDirectory) { "$I18N is not a directory: $dir" }
            dir
        },
        {
            val out = scan.file(OUT).bind()
            ensure(!out.isDirectory) { "$OUT must not be a directory: $out" }
            out
        },
        {
            ensure(scan.unknown.isEmpty()) {
                "unknown argument(s): ${scan.unknown.joinToString { "'$it'" }}"
            }
        },
    ) { dataFile, i18nDirectory, outputFile, _ ->
        Args.Input(dataFile, i18nDirectory, outputFile)
    }
}

private fun Scan.file(flag: String) = either {
    val values = flags[flag].orEmpty()
    ensure(values.isNotEmpty()) { "$flag is required" }
    ensure(values.size == 1) { "$flag specified more than once" }
    File(ensureNotNull(values.single()) { "$flag requires a value" })
}
