package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.shared.ui.utility.ContrastContentColor.hslToColor
import kotlin.math.pow

@Composable
fun Color.rememberContrastContentColor(minContrast: Double = 4.5): Color =
    remember(this, minContrast) { contrastContentColor(minContrast) }

fun Color.contrastContentColor(minContrast: Double = 4.5): Color =
    ContrastContentColor.calculate(this, minContrast)

object ContrastContentColor {
    /**
     * Calculates a content color that achieves at least [minContrast] (WCAG contrast ratio) against
     * [color]. Defaults to WCAG AA (4.5:1). Use 7.0 for WCAG AAA.
     *
     * Hue is stored in degrees (0–360) throughout this class and normalized to 0–1 only inside
     * [hslToColor].
     */
    fun calculate(color: Color, minContrast: Double = 4.5): Color {
        val (h, s, l) = rgbToHsl(color.red, color.green, color.blue)
        val bgLuminance = wcagLuminance(color.red, color.green, color.blue)
        val goingDark = l > 0.5f

        // When going dark: candidate range is dark tones (0.08–0.45).
        // When going light: candidate range is light tones (0.55–0.92).
        val lightnessBounds = if (goingDark) 0.08f..0.45f else 0.55f..0.92f
        val adjustedS = if (goingDark) s * 0.8f else s

        var low = lightnessBounds.start
        var high = lightnessBounds.endInclusive

        // Check feasibility at the hardest-to-contrast boundary:
        //   going dark → the lightest dark tone (high = 0.45) is closest to the background
        //   going light → the darkest light tone (low = 0.55) is closest to the background
        val hardBoundaryL = if (goingDark) high else low
        val hardBoundaryColor = hslToColor(h, adjustedS, hardBoundaryL)
        val hardBoundaryLuminance = wcagLuminance(hardBoundaryColor)
        if (wcagContrastRatio(bgLuminance, hardBoundaryLuminance) < minContrast) {
            // Even the closest-to-background edge doesn't reach minContrast; fall back to
            // a near-achromatic extreme that almost always passes.
            return hslToColor(h, s * 0.2f, if (goingDark) 0.1f else 0.9f)
        }

        // Binary search: find the boundary between passing and failing contrast.
        // After convergence:
        //   going dark → `high` is the lightest tone that still passes (most contrast = low)
        //                 but we want the extreme (most contrastful = low)
        //   going light → `low` is the darkest tone that still passes
        //                 but we want the extreme (most contrastful = high)
        // So we return `low` for light candidates and `high` for dark candidates.
        repeat(20) {
            val mid = (low + high) / 2f
            val ratio = wcagContrastRatio(bgLuminance, wcagLuminance(hslToColor(h, adjustedS, mid)))
            if (goingDark) {
                // Darker tones have more contrast against a light background.
                // Move high down to find the lightest passing tone;
                // the darkest extreme (low) is already guaranteed to pass.
                if (ratio >= minContrast) high = mid else low = mid
            } else {
                // Lighter tones have more contrast against a dark background.
                // Move low up to find the darkest passing tone;
                // the lightest extreme (high) is already guaranteed to pass.
                if (ratio >= minContrast) low = mid else high = mid
            }
        }

        // Return the most contrastful end of the passing range.
        return hslToColor(h, adjustedS, if (goingDark) low else high)
    }

    private fun linearize(component: Float): Double =
        if (component <= 0.04045f) component / 12.92
        else ((component + 0.055f) / 1.055f).toDouble().pow(2.4)

    private fun wcagLuminance(r: Float, g: Float, b: Float): Double =
        0.2126 * linearize(r) + 0.7152 * linearize(g) + 0.0722 * linearize(b)

    private fun wcagLuminance(color: Color): Double =
        wcagLuminance(color.red, color.green, color.blue)

    private fun wcagContrastRatio(l1: Double, l2: Double): Double {
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /** [h] is in degrees (0–360), [s] and [l] are in 0–1. */
    private data class Hsl(val h: Float, val s: Float, val l: Float)

    private fun rgbToHsl(r: Float, g: Float, b: Float): Hsl {
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val l = (max + min) / 2f
        if (max == min) return Hsl(0f, 0f, l)
        val d = max - min
        val s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
        val h =
            when (max) {
                r -> ((g - b) / d + if (g < b) 6f else 0f) / 6f
                g -> ((b - r) / d + 2f) / 6f
                else -> ((r - g) / d + 4f) / 6f
            }
        // Store hue in degrees for clarity; hslToColor normalises it back to 0–1.
        return Hsl(h * 360f, s, l)
    }

    /** Expects [h] in degrees (0–360), [s] and [l] in 0–1. */
    private fun hslToColor(h: Float, s: Float, l: Float): Color {
        if (s == 0f) return Color(l, l, l)

        fun hue2rgb(p: Float, q: Float, t: Float): Float {
            val t2 = t.mod(1f)
            return when {
                t2 < 1f / 6f -> p + (q - p) * 6f * t2
                t2 < 1f / 2f -> q
                t2 < 2f / 3f -> p + (q - p) * (2f / 3f - t2) * 6f
                else -> p
            }
        }

        val q = if (l < 0.5f) l * (1f + s) else l + s - l * s
        val p = 2f * l - q
        val hNorm = h / 360f // degrees → 0–1
        return Color(
            red = hue2rgb(p, q, hNorm + 1f / 3f),
            green = hue2rgb(p, q, hNorm),
            blue = hue2rgb(p, q, hNorm - 1f / 3f),
        )
    }
}
