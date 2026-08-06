package com.maksimowiczm.foodyou.app

import org.junit.AssumptionViolatedException

internal fun unavailable(message: String?): Nothing = throw AssumptionViolatedException(message)
