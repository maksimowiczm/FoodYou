package com.maksimowiczm.foodyou.common.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EnergyTest {
    @Test
    fun convert_kilojoules_to_kilocalories() {
        val value = Energy.kilojoules(418.4)

        assertClose(100.0, value.toDouble(EnergyUnit.Kilocalories))
    }

    @Test
    fun convert_kilocalories_to_kilojoules() {
        val value = Energy.kilocalories(100.0)

        assertClose(418.4, value.toDouble(EnergyUnit.Kilojoules))
    }

    @Test
    fun convert_using_extension_properties() {
        val value = 250.0.kilocalories

        assertClose(1046.0, value.kilojoules)
    }

    @Test
    fun preserve_source_unit_from_constructor() {
        val value = Energy.kilojoules(12.0)

        assertEquals(EnergyUnit.Kilojoules, value.unit)
    }

    @Test
    fun preserve_left_unit_for_arithmetic_results() {
        val sum = 100.0.kilocalories + 418.4.kilojoules

        assertEquals(EnergyUnit.Kilocalories, sum.unit)
    }

    @Test
    fun add_values_with_different_source_units() {
        val sum = 100.0.kilocalories + 418.4.kilojoules

        assertClose(200.0, sum.toDouble(EnergyUnit.Kilocalories))
    }

    @Test
    fun compare_by_normalized_kilocalories() {
        assertTrue(110.0.kilocalories > 418.4.kilojoules)
    }

    @Test
    fun convert_with_in_unit_preserves_amount() {
        val value = 500.0.kilojoules

        val converted = value.inUnit(EnergyUnit.Kilocalories)

        assertEquals(EnergyUnit.Kilocalories, converted.unit)
        assertClose(value.kilocalories, converted.kilocalories)
    }

    @Test
    fun multiply_scales_converted_value() {
        val doubled = Energy.kilojoules(2.0) * 2

        assertClose(4.0, doubled.toDouble(EnergyUnit.Kilojoules))
    }

    @Test
    fun divide_by_scale_returns_expected_value() {
        val halved = Energy.kilojoules(10.0) / 2

        assertClose(5.0, halved.toDouble(EnergyUnit.Kilojoules))
    }

    @Test
    fun divide_by_energy_returns_ratio() {
        val ratio = 200.0.kilocalories / 418.4.kilojoules

        assertClose(2.0, ratio)
    }

    @Test
    fun equals_and_hashcode_match_for_equivalent_units() {
        val lhs = 100.0.kilocalories
        val rhs = 418.4.kilojoules

        assertEquals(lhs.kilocalories, rhs.kilocalories, 1E09)
    }

    @Test
    fun to_string_uses_selected_unit_symbol() {
        val value = Energy.kilojoules(10.0)

        assertEquals("10.0 kJ", value.toString())
    }

    @Test
    fun reject_negative_energy() {
        assertFailsWith<IllegalArgumentException> { Energy.kilocalories(-1.0) }
    }

    @Test
    fun reject_non_finite_energy() {
        assertFailsWith<IllegalArgumentException> { Energy.kilocalories(Double.NaN) }
        assertFailsWith<IllegalArgumentException> { Energy.kilojoules(Double.POSITIVE_INFINITY) }
    }

    @Test
    fun reject_subtraction_result_below_zero() {
        assertFailsWith<IllegalArgumentException> { 10.0.kilocalories - 11.0.kilocalories }
    }

    @Test
    fun reject_invalid_scale_operations() {
        assertFailsWith<IllegalArgumentException> { 10.0.kilocalories * -1 }
        assertFailsWith<IllegalArgumentException> { 10.0.kilocalories * Double.NaN }
        assertFailsWith<IllegalArgumentException> { 10.0.kilocalories / 0 }
        assertFailsWith<IllegalArgumentException> { 10.0.kilocalories / -1 }
        assertFailsWith<IllegalArgumentException> { 10.0.kilocalories / Double.POSITIVE_INFINITY }
    }

    @Test
    fun reject_division_by_zero_energy() {
        assertFailsWith<IllegalArgumentException> { 10.0.kilocalories / 0.0.kilocalories }
    }

    @Test
    fun extension_factories_match_companion_factories() {
        assertEquals(10.kilocalories, Energy.kilocalories(10.0))
        assertEquals(10.kilojoules, Energy.kilojoules(10.0))
        assertEquals(10.0.toEnergy(EnergyUnit.Kilocalories), Energy.kilocalories(10.0))
    }

    private fun assertClose(expected: Double, actual: Double, tolerance: Double = 1e-9) {
        assertEquals(expected, actual, tolerance)
    }
}
