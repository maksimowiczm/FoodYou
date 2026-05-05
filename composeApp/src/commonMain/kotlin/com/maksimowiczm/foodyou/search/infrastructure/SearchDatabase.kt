package com.maksimowiczm.foodyou.search.infrastructure

import com.maksimowiczm.foodyou.search.infrastructure.history.SearchHistoryDao

interface SearchDatabase {
    val searchDao: SearchDao
    val searchHistoryDao: SearchHistoryDao
}
