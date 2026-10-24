package com.maksimowiczm.foodyou.fooddatacentral.domain

import com.maksimowiczm.foodyou.common.domain.crypto.SoftwareEncrypted

data class FoodDataCentralSettings(val remoteEnabled: Boolean, val apiKey: SoftwareEncrypted?)
