package com.maksimowiczm.foodyou.feature.diary.ui

import com.maksimowiczm.foodyou.feature.diary.ui.ValueStatus.Achieved
import com.maksimowiczm.foodyou.feature.diary.ui.ValueStatus.Exceeded
import com.maksimowiczm.foodyou.feature.diary.ui.ValueStatus.Remaining

enum class ValueStatus {
    Remaining,
    Achieved,
    Exceeded
}

fun <N : Comparable<N>> N.asValueStatus(goal: N) = when {
    this < goal -> Remaining
    this > goal -> Exceeded
    else -> Achieved
}
