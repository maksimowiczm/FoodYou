package com.maksimowiczm.foodyou.common.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class VolumeTest {
    @Test
    fun convert_fluid_ounces_to_milliliters() {
        val value = Volume.fluidOunces(1.0)

        assertClose(29.5735, value.toDouble(VolumeUnit.Milliliters))
    }

    @Test
    fun multiply_scales_converted_value() {
        val doubled = Volume.fluidOunces(2.0) * 2.0

        assertClose(4.0, doubled.toDouble(VolumeUnit.FluidOunces))
    }

    @Test
    fun divide_by_scale_returns_expected_value() {
        val halved = Volume.fluidOunces(8.0) / 2.0

        assertClose(4.0, halved.toDouble(VolumeUnit.FluidOunces))
    }

    @Test
    fun divide_by_volume_returns_ratio() {
        val ratio = 200.milliliters / 1.fluidOunces

        assertClose(6.76281130066, ratio)
    }

    @Test
    fun compare_by_normalized_milliliters() {
        assertTrue(100.0.milliliters > 3.0.fluidOunces)
    }

    @Test
    fun convert_with_in_unit_preserves_amount() {
        val value = 100.0.fluidOunces

        val converted = value.inUnit(VolumeUnit.Milliliters)

        assertEquals(VolumeUnit.Milliliters, converted.unit)
        assertClose(value.milliliters, converted.milliliters)
    }

    @Test
    fun equals_and_hashcode_match_for_equivalent_units() {
        val lhs = 1.0.fluidOunces
        val rhs = 29.5735.milliliters

        assertEquals(lhs, rhs)
        assertEquals(lhs.hashCode(), rhs.hashCode())
    }

    @Test
    fun to_string_uses_selected_unit_symbol() {
        val value = Volume.fluidOunces(10.0)

        assertEquals("10.0 floz", value.toString())
    }

    @Test
    fun reject_negative_volume() {
        assertFailsWith<IllegalArgumentException> { Volume.milliliters(-1.0) }
    }

    @Test
    fun reject_non_finite_volume() {
        assertFailsWith<IllegalArgumentException> { Volume.milliliters(Double.NaN) }
        assertFailsWith<IllegalArgumentException> { Volume.fluidOunces(Double.POSITIVE_INFINITY) }
        assertFailsWith<IllegalArgumentException> { Volume.fluidOunces(Double.NEGATIVE_INFINITY) }
    }

    @Test
    fun reject_subtraction_result_below_zero() {
        assertFailsWith<IllegalArgumentException> { 100.0.milliliters - 101.0.milliliters }
    }

    @Test
    fun reject_invalid_scale_operations() {
        assertFailsWith<IllegalArgumentException> { 10.0.milliliters * -1.0 }
        assertFailsWith<IllegalArgumentException> { 10.0.milliliters * Double.NaN }
        assertFailsWith<IllegalArgumentException> { 10.0.milliliters / 0.0 }
        assertFailsWith<IllegalArgumentException> { 10.0.milliliters / -1.0 }
        assertFailsWith<IllegalArgumentException> { 10.0.milliliters / Double.POSITIVE_INFINITY }
    }

    @Test
    fun reject_division_by_zero_volume() {
        assertFailsWith<IllegalArgumentException> { 10.0.milliliters / 0.0.milliliters }
    }

    @Test
    fun extension_factories_match_companion_factories() {
        assertEquals(10.0.toVolume(VolumeUnit.Milliliters), Volume.milliliters(10.0))
        assertEquals(10.0.milliliters, Volume.milliliters(10.0))
        assertEquals(10.0.fluidOunces, Volume.fluidOunces(10.0))
    }

    @Test
    fun parse_valid_inputs() {
        assertEquals(100.0.milliliters, Volume.parse("100ml"))
        assertEquals(100.0.milliliters, Volume.parse("100 ml"))
        assertEquals(100.0.milliliters, Volume.parse("100 milliliters"))
        assertEquals(100.0.milliliters, Volume.parse("100 mlt"))
        assertEquals(100.0.milliliters, Volume.parse("100 ml."))
        assertEquals(100.0.milliliters, Volume.parse("100 millilitre"))
        assertEquals(100.0.milliliters, Volume.parse("100 cc"))
        assertEquals(100.0.milliliters, Volume.parse("100 cm3"))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5floz"))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 fl oz"))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 fl.oz"))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 fl. oz."))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 fluid ounces"))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 oz"))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 oz."))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 fluid oz"))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 fl ounce"))
        assertEquals(10.5.fluidOunces, Volume.parse("10.5 oz fl"))
    }

    @Test
    fun parse_case_insensitivity() {
        assertEquals(100.0.milliliters, Volume.parse("100 ML"))
        assertEquals(10.0.fluidOunces, Volume.parse("10 FL OZ"))
    }

    @Test
    fun parse_or_null_returns_null_for_invalid_input() {
        assertEquals(null, Volume.parseOrNull("100"))
        assertEquals(null, Volume.parseOrNull("ml"))
        assertEquals(null, Volume.parseOrNull("abc"))
        assertEquals(null, Volume.parseOrNull("-10ml"))
    }

    @Test
    fun parse_throws_for_invalid_input() {
        assertFailsWith<IllegalArgumentException> { Volume.parse("invalid") }
    }

    private fun assertClose(expected: Double, actual: Double, tolerance: Double = 1e-7) {
        assertEquals(expected, actual, tolerance)
    }
}
