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
        val servingQuantity = 30.grams.toAbsoluteQuantity()
        val packageQuantity = 500.grams.toAbsoluteQuantity()

        val result =
            FoodSnapshotQuantityUpdateService.map(
                quantity = AbsoluteQuantity.Weight(weight),
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(weight, result.absoluteWeight)
        assertEquals(servingQuantity, result.servingQuantity)
        assertEquals(packageQuantity, result.packageQuantity)
    }

    @Test
    fun map_should_map_volume_quantity() {
        val volume = 200.milliliters
        val servingQuantity = 50.milliliters.toAbsoluteQuantity()
        val packageQuantity = 1000.milliliters.toAbsoluteQuantity()

        val result =
            FoodSnapshotQuantityUpdateService.map(
                quantity = AbsoluteQuantity.Volume(volume),
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
            )

        assertIs<FoodSnapshotQuantity.Volume>(result)
        assertEquals(volume, result.absoluteVolume)
        assertEquals(servingQuantity, result.servingQuantity)
        assertEquals(packageQuantity, result.packageQuantity)
    }

    @Test
    fun map_should_map_package_quantity() {
        val packages = 2.5
        val servingQuantity = 20.grams.toAbsoluteQuantity()
        val packageQuantity = 400.grams.toAbsoluteQuantity()

        val result =
            FoodSnapshotQuantityUpdateService.map(
                quantity = PackageQuantity(packages),
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
            )

        assertIs<FoodSnapshotQuantity.Package>(result)
        assertEquals(packages, result.packages)
        assertEquals(servingQuantity, result.servingQuantity)
        assertEquals(packageQuantity, result.packageQuantity)
        assertEquals(AbsoluteQuantity.Weight(1000.grams), result.absoluteQuantity)
    }

    @Test
    fun map_should_throw_if_package_quantity_is_null_for_package_quantity() {
        assertFailsWith<IllegalArgumentException> {
            FoodSnapshotQuantityUpdateService.map(
                quantity = PackageQuantity(1.0),
                servingQuantity = null,
                packageQuantity = null,
            )
        }
    }

    @Test
    fun map_should_map_serving_quantity() {
        val servings = 3.0
        val servingQuantity = 45.grams.toAbsoluteQuantity()
        val packageQuantity = 900.grams.toAbsoluteQuantity()

        val result =
            FoodSnapshotQuantityUpdateService.map(
                quantity = ServingQuantity(servings),
                servingQuantity = servingQuantity,
                packageQuantity = packageQuantity,
            )

        assertIs<FoodSnapshotQuantity.Serving>(result)
        assertEquals(servings, result.servings)
        assertEquals(servingQuantity, result.servingQuantity)
        assertEquals(packageQuantity, result.packageQuantity)
        assertEquals(AbsoluteQuantity.Weight(135.grams), result.absoluteQuantity)
    }

    @Test
    fun map_should_throw_if_serving_quantity_is_null_for_serving_quantity() {
        assertFailsWith<IllegalArgumentException> {
            FoodSnapshotQuantityUpdateService.map(
                quantity = ServingQuantity(1.0),
                servingQuantity = null,
                packageQuantity = null,
            )
        }
    }

    @Test
    fun update_should_update_serving_quantity() {
        val current =
            FoodSnapshotQuantity.Serving(
                servings = 2.0,
                servingQuantity = 30.grams.toAbsoluteQuantity(),
                packageQuantity = null,
            )
        val newServingQuantity = 40.grams.toAbsoluteQuantity()

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingQuantity = newServingQuantity,
                packageQuantity = null,
            )

        assertIs<FoodSnapshotQuantity.Serving>(result)
        assertEquals(newServingQuantity, result.servingQuantity)
        assertEquals(AbsoluteQuantity.Weight(80.grams), result.absoluteQuantity)
    }

    @Test
    fun update_should_update_package_quantity() {
        val current =
            FoodSnapshotQuantity.Package(
                packages = 0.5,
                packageQuantity = 500.grams.toAbsoluteQuantity(),
                servingQuantity = null,
            )
        val newPackageQuantity = 600.grams.toAbsoluteQuantity()

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingQuantity = null,
                packageQuantity = newPackageQuantity,
            )

        assertIs<FoodSnapshotQuantity.Package>(result)
        assertEquals(newPackageQuantity, result.packageQuantity)
        assertEquals(AbsoluteQuantity.Weight(300.grams), result.absoluteQuantity)
    }

    @Test
    fun update_should_convert_serving_to_weight_if_serving_quantity_becomes_null() {
        val current =
            FoodSnapshotQuantity.Serving(
                servings = 2.0,
                servingQuantity = 30.grams.toAbsoluteQuantity(),
                packageQuantity = null,
            )

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingQuantity = null,
                packageQuantity = null,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(60.grams, result.absoluteWeight)
    }

    @Test
    fun update_should_convert_package_to_weight_if_package_quantity_becomes_null() {
        val current =
            FoodSnapshotQuantity.Package(
                packages = 0.5,
                packageQuantity = 500.grams.toAbsoluteQuantity(),
                servingQuantity = null,
            )

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingQuantity = null,
                packageQuantity = null,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(250.grams, result.absoluteWeight)
    }

    @Test
    fun update_should_update_weight_quantity_metadata() {
        val current =
            FoodSnapshotQuantity.Weight(
                absoluteWeight = 100.grams,
                servingQuantity = 20.grams.toAbsoluteQuantity(),
                packageQuantity = 200.grams.toAbsoluteQuantity(),
            )
        val newServingQuantity = 25.grams.toAbsoluteQuantity()
        val newPackageQuantity = 250.grams.toAbsoluteQuantity()

        val result =
            FoodSnapshotQuantityUpdateService.update(
                current = current,
                servingQuantity = newServingQuantity,
                packageQuantity = newPackageQuantity,
            )

        assertIs<FoodSnapshotQuantity.Weight>(result)
        assertEquals(100.grams, result.absoluteWeight)
        assertEquals(newServingQuantity, result.servingQuantity)
        assertEquals(newPackageQuantity, result.packageQuantity)
    }
}
