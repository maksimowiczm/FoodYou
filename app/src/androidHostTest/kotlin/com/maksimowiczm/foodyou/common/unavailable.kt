package com.maksimowiczm.foodyou.common

import org.junit.AssumptionViolatedException

internal fun unavailable(message: String?): Nothing = throw AssumptionViolatedException(message)
