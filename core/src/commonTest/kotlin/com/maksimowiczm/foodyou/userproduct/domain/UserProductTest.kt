package com.maksimowiczm.foodyou.userproduct.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class UserProductTest {
    private val userProductId = UserProductId()
    private val foodName = FoodName(fallback = "Apple")
    private val nutritionFacts = NutritionFacts()
    private val userProduct =
        UserProduct(
            id = userProductId,
            name = foodName,
            brand = "Test Brand",
            barcode = UserProductBarcode("123456789"),
            note = "Test Note",
            image = null,
            nutritionFacts = nutritionFacts,
            servingQuantity = null,
            packageQuantity = null,
            isLiquid = false,
        )

    @Test
    fun reject_blank_brand_name() {
        assertFailsWith<IllegalArgumentException> { userProduct.copy(brand = " ") }
    }

    @Test
    fun reject_blank_note() {
        assertFailsWith<IllegalArgumentException> { userProduct.copy(note = " ") }
    }

    @Test
    fun allow_null_brand_and_note() {
        val minimalProduct = userProduct.copy(brand = null, note = null)
        assertNull(minimalProduct.brand)
        assertNull(minimalProduct.note)
    }

    @Test
    fun barcode_rejects_blank() {
        assertFailsWith<IllegalArgumentException> { UserProductBarcode("") }
        assertFailsWith<IllegalArgumentException> { UserProductBarcode(" ") }
    }

    @Test
    fun barcode_rejects_non_digits() {
        assertFailsWith<IllegalArgumentException> { UserProductBarcode("123a456") }
    }

    @Test
    fun barcode_accepts_digits() {
        val barcode = UserProductBarcode("0123456789")
        assertEquals("0123456789", barcode.value)
    }

    @Test
    fun create_returns_created_event() {
        val events = (null as UserProduct?).decide(UserProductCommand.Create(userProduct))

        assertEquals(1, events.size)
        val event = assertIs<UserProductCreatedEvent>(events[0])
        assertEquals(userProduct, event.product)
    }

    @Test
    fun update_returns_updated_event_when_changed() {
        val updatedName = FoodName(fallback = "Banana")

        val events = userProduct.decide(UserProductCommand.Update { it.copy(name = updatedName) })

        assertEquals(1, events.size)
        val event = assertIs<UserProductUpdatedEvent>(events[0])
        assertEquals(updatedName, event.product.name)
    }

    @Test
    fun update_returns_empty_list_when_not_changed() {
        val events = userProduct.decide(UserProductCommand.Update { it })
        assertEquals(0, events.size)
    }

    @Test
    fun remove_returns_deleted_event() {
        val events = userProduct.decide(UserProductCommand.Remove(DeleteStrategy.Delete))

        assertEquals(1, events.size)
        val event = assertIs<UserProductDeletedEvent>(events[0])
        assertEquals(userProductId, event.userProductId)
        assertEquals(DeleteStrategy.Delete, event.strategy)
    }

    @Test
    fun apply_created_event() {
        val event = UserProductCreatedEvent(userProduct)
        val result = null.apply(event)
        assertEquals(userProduct, result)
    }

    @Test
    fun apply_updated_event() {
        val updatedProduct = userProduct.copy(brand = "Updated Brand")
        val event = UserProductUpdatedEvent(updatedProduct)
        val result = userProduct.apply(event)
        assertEquals(updatedProduct, result)
    }

    @Test
    fun apply_deleted_event() {
        val event = UserProductDeletedEvent(userProductId, DeleteStrategy.Delete)
        val result = userProduct.apply(event)
        assertNull(result)
    }

    @Test
    fun toUserProduct_reconstructs_state() {
        val updatedProduct = userProduct.copy(brand = "Updated Brand")
        val events =
            listOf(
                UserProductCreatedEvent(userProduct),
                UserProductUpdatedEvent(updatedProduct),
            )

        val result = events.toUserProduct()
        assertEquals(updatedProduct, result)
    }

    @Test
    fun toUserProduct_returns_null_if_deleted() {
        val events =
            listOf(
                UserProductCreatedEvent(userProduct),
                UserProductDeletedEvent(
                    userProductId,
                    DeleteStrategy.Delete,
                ),
            )

        val result = events.toUserProduct()
        assertNull(result)
    }
}
