package com.maksimowiczm.foodyou.core.feature.about.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.maksimowiczm.foodyou.core.R

class AndroidLinkHandler(private val context: Context) : LinkHandler {
    override fun openGithubIssue() =
        openLink(context.resources.getString(R.string.link_github_issue))

    override fun openGithubRepository() {
        openLink(context.resources.getString(R.string.link_github_repository))
    }

    override fun openGithubReadme() = openGithubRepository()

    private fun openLink(link: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            link.toUri()
        ).apply {
            flags += Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }
}
