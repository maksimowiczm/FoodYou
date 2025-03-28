package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases

import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import kotlinx.coroutines.flow.first

class DeleteProductCase(
    private val productRepository: ProductRepository,
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke(productId: Long) {
        productRepository.deleteProduct(productId)
    }

    suspend operator fun invoke(measurementId: MeasurementId) {
        measurementId as MeasurementId.Product

        val productId =
            measurementRepository.observeMeasurementById(measurementId).first()?.product?.id

        if (productId != null) {
            productRepository.deleteProduct(productId)
        }
    }
}
