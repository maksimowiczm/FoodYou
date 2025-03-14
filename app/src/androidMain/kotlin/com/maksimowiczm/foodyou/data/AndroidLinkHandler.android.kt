package com.maksimowiczm.foodyou.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

val Context.linkHandler: LinkHandler
    get() = LinkHandler { link ->
        val intent = Intent(
            Intent.ACTION_VIEW,
            link.toUri()
        ).apply {
            flags += Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(intent)
    }
