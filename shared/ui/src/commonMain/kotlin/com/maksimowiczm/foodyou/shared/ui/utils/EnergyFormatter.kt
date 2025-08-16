package com.maksimowiczm.foodyou.shared.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

interface EnergyFormatter {

    /**
     * Formats the given energy value in kilocalories (kcal) to a user-friendly string.
     *
     * @param energy The energy value in kilocalories to format.
     * @param format The format string to use for formatting the energy value, defaulting to "%.2f".
     * @param withSuffix Whether to append the energy unit suffix (e.g., "kcal") to the formatted
     *   string.
     * @return A string representation of the energy value, formatted for display.
     */
    @Composable
    fun formatEnergy(energy: Double, format: String = "%.2f", withSuffix: Boolean = true): String

    @Composable fun formatEnergy(energy: Int, withSuffix: Boolean = true): String

    @Composable fun suffix(): String

    /**
     * Formats the given energy value in kilocalories (kcal) to a user-friendly string, specifically
     * for displaying the remaining energy left in a goal or plan.
     *
     * @param left The energy value in kilocalories to format.
     * @return A string representation of the energy value, formatted for display.
     */
    @Composable fun energyLeft(left: Int): String

    /**
     * Formats the given energy value in kilocalories (kcal) to a user-friendly string, specifically
     * for displaying the amount of energy that has been exceeded.
     *
     * @param exceeded The energy value in kilocalories that has been exceeded.
     * @return A string representation of the energy value, formatted for display.
     */
    @Composable fun energyExceeded(exceeded: Int): String

    companion object {
        val kilocalories: EnergyFormatter
            get() = KilocaloriesFormatter()

        val kilojoules: EnergyFormatter
            get() = KilojoulesFormatter()
    }
}

private class KilocaloriesFormatter : EnergyFormatter {

    @Composable
    override fun formatEnergy(energy: Double, format: String, withSuffix: Boolean): String =
        buildString {
            append(energy.formatClipZeros(format))
            if (withSuffix) {
                append(" ")
                append(suffix())
            }
        }

    @Composable
    override fun formatEnergy(energy: Int, withSuffix: Boolean): String = buildString {
        append(energy)
        if (withSuffix) {
            append(" ")
            append(suffix())
        }
    }

    @Composable override fun suffix(): String = stringResource(Res.string.unit_kcal)

    @Composable
    override fun energyLeft(left: Int): String =
        pluralStringResource(Res.plurals.neutral_remaining_calories, left, left)

    @Composable
    override fun energyExceeded(exceeded: Int): String =
        pluralStringResource(Res.plurals.negative_exceeded_by_calories, exceeded, exceeded)
}

private class KilojoulesFormatter : EnergyFormatter {

    @Composable
    override fun formatEnergy(energy: Double, format: String, withSuffix: Boolean): String =
        buildString {
            val value = toKj(energy)
            append(value.formatClipZeros(format))
            if (withSuffix) {
                append(" ")
                append(suffix())
            }
        }

    @Composable
    override fun formatEnergy(energy: Int, withSuffix: Boolean): String = buildString {
        val value = toKj(energy)
        append(value.roundToInt())
        if (withSuffix) {
            append(" ")
            append(suffix())
        }
    }

    @Composable override fun suffix(): String = stringResource(Res.string.unit_kilojoules)

    @Composable
    override fun energyLeft(left: Int): String =
        pluralStringResource(
            Res.plurals.neutral_remaining_kilojoules,
            left,
            toKj(left).roundToInt(),
        )

    @Composable
    override fun energyExceeded(exceeded: Int): String =
        pluralStringResource(
            Res.plurals.negative_exceeded_by_kilojoules,
            exceeded,
            toKj(exceeded).roundToInt(),
        )

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
