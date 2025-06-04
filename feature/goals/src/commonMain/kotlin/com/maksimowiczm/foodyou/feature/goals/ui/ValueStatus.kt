package com.maksimowiczm.foodyou.feature.goals.ui

import com.maksimowiczm.foodyou.feature.goals.ui.ValueStatus.Achieved
import com.maksimowiczm.foodyou.feature.goals.ui.ValueStatus.Exceeded
import com.maksimowiczm.foodyou.feature.goals.ui.ValueStatus.Remaining

internal enum class ValueStatus {
    Remaining,
    Achieved,
    Exceeded
}

internal fun <N : Comparable<N>> N.asValueStatus(goal: N) = when {
    this < goal -> Remaining
    this > goal -> Exceeded
    else -> Achieved
}
