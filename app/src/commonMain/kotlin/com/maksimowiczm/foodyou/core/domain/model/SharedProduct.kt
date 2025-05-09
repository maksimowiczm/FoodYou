package com.maksimowiczm.foodyou.core.domain.model

data class SharedProduct(
    val name: String?,
    val brand: String?,
    val nutrients: SharedNutrients,
    val packageWeight: PortionWeight.Package?,
    val servingWeight: PortionWeight.Serving?
)
