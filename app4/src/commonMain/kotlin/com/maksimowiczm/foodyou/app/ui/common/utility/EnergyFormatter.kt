package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

interface EnergyFormatter {

    /**
     * Formats the given energy value in kilocalories (kcal) to a user-friendly string.
     *
     * @param energy The energy value in kilocalories to format.
     * @param format The format string to use for formatting the energy value, defaulting to "%.2f".
     * @return A string representation of the energy value, formatted for display.
     */
    @Composable fun formatEnergy(energy: Double?, format: String = "%.2f"): String

    /**
     * Formats the given energy value in kilocalories (kcal) to a user-friendly string.
     *
     * @param energy The energy value in kilocalories to format.
     * @return A string representation of the energy value, formatted for display.
     */
    @Composable fun formatEnergy(energy: Int?): String

    /**
     * Returns the suffix used for displaying energy values.
     *
     * @return The suffix as a string.
     */
    @Composable fun suffix(): String

    companion object {
        val kilocalories: EnergyFormatter
            get() = KilocaloriesFormatter()

        val kilojoules: EnergyFormatter
            get() = KilojoulesFormatter()
    }
}

private class KilocaloriesFormatter : EnergyFormatter {

    @Composable
    override fun formatEnergy(energy: Double?, format: String): String {
        val suffix = suffix()
        return remember(suffix, energy, format) {
            buildString {
                when (energy) {
                    null -> append("?")
                    else -> append(energy.formatClipZeros(format))
                }
                append(" ")
                append(suffix)
            }
        }
    }

    @Composable
    override fun formatEnergy(energy: Int?): String {
        val suffix = suffix()
        return remember(suffix, energy) {
            buildString {
                when (energy) {
                    null -> append("?")
                    else -> append(energy)
                }
                append(" ")
                append(suffix)
            }
        }
    }

    @Composable override fun suffix(): String = stringResource(Res.string.unit_kcal)
}

private class KilojoulesFormatter : EnergyFormatter {

    @Composable
    override fun formatEnergy(energy: Double?, format: String): String {
        val suffix = suffix()
        return remember(suffix, energy, format) {
            buildString {
                when (energy) {
                    null -> append("?")
                    else -> append(toKj(energy).formatClipZeros(format))
                }
                append(" ")
                append(suffix)
            }
        }
    }

    @Composable
    override fun formatEnergy(energy: Int?): String {
        val suffix = suffix()
        return remember(suffix, energy) {
            buildString {
                when (energy) {
                    null -> append("?")
                    else -> append(toKj(energy).roundToInt())
                }
                append(" ")
                append(suffix)
            }
        }
    }

    @Composable override fun suffix(): String = stringResource(Res.string.unit_kilojoules)

    private fun toKj(energy: Number): Double = energy.toDouble() * KILOJOULE_CONVERSION_FACTOR

    private companion object {
        private const val KILOJOULE_CONVERSION_FACTOR = 4.184 // 1 kcal = 4.184 kJ
    }
}

val LocalEnergyFormatter = staticCompositionLocalOf<EnergyFormatter> { KilocaloriesFormatter() }

@Composable
fun EnergyFormatterProvider(energyFormatter: EnergyFormatter, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalEnergyFormatter provides energyFormatter) { content() }
}
