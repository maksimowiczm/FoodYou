package com.maksimowiczm.foodyou.shared.common.infrastructure.system

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.system.Country
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.system.SystemDetails

expect class SystemDetailsImpl : SystemDetails {
    override val defaultCountry: Country
}
