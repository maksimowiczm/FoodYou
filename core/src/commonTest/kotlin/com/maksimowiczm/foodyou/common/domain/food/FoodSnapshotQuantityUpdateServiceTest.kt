package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class FoodSnapshotQuantityUpdateServiceTest {

    @Test
    fun map_should_map_weight_quantity() {
        val weight = 150.grams
        val servingWeight = 30.grams
        val packageWeight = 500.grams

        val result =
            FoodSnapshotQuantityUpdateService.map(
                quantity = AbsoluteQuantity.Weight(weight),
                servingWeight = servingWeight,
                packageWeight = packageWeight,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(weight, result.absoluteWeight)
        assertEquals(servingWeight, result.servingWeight)
        assertEquals(packageWeight, result.packageWeight)
    }

    @Test
    fun map_should_map_volume_quantity_to_weight() {
        val volume = 200.milliliters
        val servingWeight = 50.grams
        val packageWeight = 1000.grams

        val result =
            FoodSnapshotQuantityUpdateService.map(
                quantity = AbsoluteQuantity.Volume(volume),
                servingWeight = servingWeight,
                packageWeight = packageWeight,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(200.grams, result.absoluteWeight)
        assertEquals(servingWeight, result.servingWeight)
        assertEquals(packageWeight, result.packageWeight)
    }

    @Test
    fun map_should_map_package_quantity() {
        val packages = 2.5
        val servingWeight = 20.grams
        val packageWeight = 400.grams

        val result =
            FoodSnapshotQuantityUpdateService.map(
                quantity = PackageQuantity(packages),
                servingWeight = servingWeight,
                packageWeight = packageWeight,
            )

        assertIs<FoodSnapshotQuantity.Package>(result)
        assertEquals(packages, result.packages)
        assertEquals(servingWeight, result.servingWeight)
        assertEquals(packageWeight, result.packageWeight)
        assertEquals(1000.grams, result.absoluteWeight)
    }

    @Test
    fun map_should_throw_if_package_weight_is_null_for_package_quantity() {
        assertFailsWith<IllegalArgumentException> {
            FoodSnapshotQuantityUpdateService.map(
                quantity = PackageQuantity(1.0),
                servingWeight = null,
                packageWeight = null,
            )
        }
    }

    @Test
    fun map_should_map_serving_quantity() {
        val servings = 3.0
        val servingWeight = 45.grams
        val packageWeight = 900.grams

        val result =
            FoodSnapshotQuantityUpdateService.map(
                quantity = ServingQuantity(servings),
                servingWeight = servingWeight,
                packageWeight = packageWeight,
            )

        assertIs<FoodSnapshotQuantity.Serving>(result)
        assertEquals(servings, result.servings)
        assertEquals(servingWeight, result.servingWeight)
        assertEquals(packageWeight, result.packageWeight)
        assertEquals(135.grams, result.absoluteWeight)
    }

    @Test
    fun map_should_throw_if_serving_weight_is_null_for_serving_quantity() {
        assertFailsWith<IllegalArgumentException> {
            FoodSnapshotQuantityUpdateService.map(
                quantity = ServingQuantity(1.0),
                servingWeight = null,
                packageWeight = null,
            )
        }
    }

    @Test
    fun update_should_update_serving_weight() {
        val current =
            FoodSnapshotQuantity.Serving(
                servings = 2.0,
                servingWeight = 30.grams,
                packageWeight = null,
            )
        val newServingWeight = 40.grams

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingWeight = newServingWeight,
                packageWeight = null,
            )

        assertIs<FoodSnapshotQuantity.Serving>(result)
        assertEquals(newServingWeight, result.servingWeight)
        assertEquals(80.grams, result.absoluteWeight)
    }

    @Test
    fun update_should_update_package_weight() {
        val current =
            FoodSnapshotQuantity.Package(
                packages = 0.5,
                packageWeight = 500.grams,
                servingWeight = null,
            )
        val newPackageWeight = 600.grams

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingWeight = null,
                packageWeight = newPackageWeight,
            )

        assertIs<FoodSnapshotQuantity.Package>(result)
        assertEquals(newPackageWeight, result.packageWeight)
        assertEquals(300.grams, result.absoluteWeight)
    }

    @Test
    fun update_should_convert_serving_to_weight_if_serving_weight_becomes_null() {
        val current =
            FoodSnapshotQuantity.Serving(
                servings = 2.0,
                servingWeight = 30.grams,
                packageWeight = null,
            )

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingWeight = null,
                packageWeight = null,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(60.grams, result.absoluteWeight)
    }

    @Test
    fun update_should_convert_package_to_weight_if_package_weight_becomes_null() {
        val current =
            FoodSnapshotQuantity.Package(
                packages = 0.5,
                packageWeight = 500.grams,
                servingWeight = null,
            )

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingWeight = null,
                packageWeight = null,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(250.grams, result.absoluteWeight)
    }

    @Test
    fun update_should_update_weight_quantity_metadata() {
        val current =
            FoodSnapshotQuantity.Weight(
                absoluteWeight = 100.grams,
                servingWeight = 20.grams,
                packageWeight = 200.grams,
            )
        val newServingWeight = 25.grams
        val newPackageWeight = 250.grams

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingWeight = newServingWeight,
                packageWeight = newPackageWeight,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(100.grams, result.absoluteWeight)
        assertEquals(newServingWeight, result.servingWeight)
        assertEquals(newPackageWeight, result.packageWeight)
    }
}
