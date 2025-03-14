package com.maksimowiczm.foodyou.data

import android.content.Context
import com.maksimowiczm.foodyou.R

class AndroidOpenSourceLinkHandler(
    private val context: Context,
    private val linkHandler: LinkHandler
) : OpenSourceLinkHandler {
    override fun openGithubIssue() =
        linkHandler.openLink(context.resources.getString(R.string.link_github_issue))

    override fun openIcons8() {
        linkHandler.openLink(context.resources.getString(R.string.link_icons8))
    }

    override fun openGithubRepository() {
        linkHandler.openLink(context.resources.getString(R.string.link_github_repository))
    }

    override fun openGithubReadme() = openGithubRepository()
}
