package com.maksimowiczm.foodyou.common.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class WeightTest {
    @Test
    fun convert_ounces_to_grams() {
        val value = Weight.ounces(1.0)

        assertClose(28.3495, value.toDouble(WeightUnit.Grams))
    }

    @Test
    fun convert_grams_to_ounces() {
        val value = Weight.grams(100.0)

        assertClose(3.527399072294044, value.toDouble(WeightUnit.Ounces))
    }

    @Test
    fun convert_milligrams_to_grams() {
        val value = Weight.milligrams(1500.0)

        assertClose(1.5, value.toDouble(WeightUnit.Grams))
    }

    @Test
    fun convert_micrograms_to_milligrams() {
        val value = Weight.micrograms(2500.0)

        assertClose(2.5, value.toDouble(WeightUnit.Milligrams))
    }

    @Test
    fun convert_using_extension_properties() {
        val value = Weight.grams(1.5)

        assertClose(1500.0, value.milligrams)
        assertClose(1_500_000.0, value.micrograms)
        assertClose(0.05291098608441066, value.ounces)
    }

    @Test
    fun add_values_with_different_source_units() {
        val sum = 100.0.grams + 1.0.ounces

        assertClose(128.3495, sum.toDouble(WeightUnit.Grams))
    }

    @Test
    fun preserve_source_unit_from_constructor() {
        val value = Weight.milligrams(500.0)

        assertEquals(WeightUnit.Milligrams, value.unit)
    }

    @Test
    fun preserve_left_unit_for_arithmetic_results() {
        val sum = 100.0.grams + 1.0.ounces

        assertEquals(WeightUnit.Grams, sum.unit)
    }

    @Test
    fun add_milligrams_and_micrograms() {
        val sum = 500.0.milligrams + 250_000.0.micrograms

        assertClose(0.75, sum.toDouble(WeightUnit.Grams))
    }

    @Test
    fun multiply_scales_converted_value() {
        val doubledOunces = Weight.ounces(2.0) * 2.0

        assertClose(4.0, doubledOunces.toDouble(WeightUnit.Ounces))
    }

    @Test
    fun divide_by_scale_returns_expected_value() {
        val halved = Weight.ounces(8.0) / 2

        assertClose(4.0, halved.toDouble(WeightUnit.Ounces))
    }

    @Test
    fun divide_by_weight_returns_ratio() {
        val ratio = 200.0.grams / 1000.0.milligrams

        assertClose(200.0, ratio)
    }

    @Test
    fun compare_by_normalized_grams() {
        assertTrue(100.0.grams > 3.0.ounces)
    }

    @Test
    fun convert_with_in_unit_preserves_amount() {
        val value = 2500.0.milligrams

        val converted = value.inUnit(WeightUnit.Grams)

        assertEquals(WeightUnit.Grams, converted.unit)
        assertClose(value.grams, converted.grams)
    }

    @Test
    fun equals_and_hashcode_match_for_equivalent_units() {
        val lhs = 100.0.grams
        val rhs = 100_000.0.milligrams

        assertEquals(lhs, rhs)
        assertEquals(lhs.hashCode(), rhs.hashCode())
    }

    @Test
    fun to_string_uses_selected_unit_symbol() {
        val value = Weight.ounces(10.0)

        assertEquals("10.0 oz", value.toString())
    }

    @Test
    fun reject_negative_weight() {
        assertFailsWith<IllegalArgumentException> { Weight.grams(-1.0) }
    }

    @Test
    fun reject_non_finite_weight() {
        assertFailsWith<IllegalArgumentException> { Weight.grams(Double.NaN) }
        assertFailsWith<IllegalArgumentException> { Weight.ounces(Double.POSITIVE_INFINITY) }
        assertFailsWith<IllegalArgumentException> { Weight.milligrams(Double.NEGATIVE_INFINITY) }
    }

    @Test
    fun reject_subtraction_result_below_zero() {
        assertFailsWith<IllegalArgumentException> { 10.0.grams - 11.0.grams }
    }

    @Test
    fun reject_invalid_scale_operations() {
        assertFailsWith<IllegalArgumentException> { 10.0.grams * -1 }
        assertFailsWith<IllegalArgumentException> { 10.0.grams * Double.NaN }
        assertFailsWith<IllegalArgumentException> { 10.0.grams / 0 }
        assertFailsWith<IllegalArgumentException> { 10.0.grams / -1 }
        assertFailsWith<IllegalArgumentException> { 10.0.grams / Double.POSITIVE_INFINITY }
    }

    @Test
    fun reject_division_by_zero_weight() {
        assertFailsWith<IllegalArgumentException> { 10.0.grams / 0.0.grams }
    }

    @Test
    fun extension_factories_match_companion_factories() {
        assertEquals(10.0.toWeight(WeightUnit.Grams), Weight.grams(10.0))
        assertEquals(10.0.grams, Weight.grams(10.0))
        assertEquals(10.0.ounces, Weight.ounces(10.0))
    }

    @Test
    fun parse_valid_inputs() {
        assertEquals(100.0.grams, Weight.parse("100g"))
        assertEquals(100.0.grams, Weight.parse("100 g"))
        assertEquals(100.0.grams, Weight.parse("100 grams"))
        assertEquals(10.5.milligrams, Weight.parse("10.5mg"))
        assertEquals(5.0.micrograms, Weight.parse("5mcg"))
        assertEquals(5.0.micrograms, Weight.parse("5ug"))
        assertEquals(5.0.micrograms, Weight.parse("5µg"))
        assertEquals(2.0.ounces, Weight.parse("2oz"))
        assertEquals(2.0.ounces, Weight.parse("2 ounces"))
    }

    @Test
    fun parse_case_insensitivity() {
        assertEquals(100.0.grams, Weight.parse("100 G"))
        assertEquals(10.0.milligrams, Weight.parse("10 MG"))
        assertEquals(5.0.micrograms, Weight.parse("5 MCG"))
        assertEquals(2.0.ounces, Weight.parse("2 OZ"))
    }

    @Test
    fun parse_or_null_returns_null_for_invalid_input() {
        assertEquals(null, Weight.parseOrNull("100"))
        assertEquals(null, Weight.parseOrNull("g"))
        assertEquals(null, Weight.parseOrNull("abc"))
        assertEquals(null, Weight.parseOrNull("100abc"))
        assertEquals(null, Weight.parseOrNull("-10g"))
    }

    @Test
    fun parse_throws_for_invalid_input() {
        assertFailsWith<IllegalArgumentException> { Weight.parse("invalid") }
    }

    private fun assertClose(expected: Double, actual: Double, tolerance: Double = 1e-9) {
        assertEquals(expected, actual, tolerance)
    }
}
