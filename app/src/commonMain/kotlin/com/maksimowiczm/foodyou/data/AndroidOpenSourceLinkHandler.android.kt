package com.maksimowiczm.foodyou.data

import android.content.Context

class AndroidOpenSourceLinkHandler(
    private val context: Context,
    private val linkHandler: LinkHandler
) : OpenSourceLinkHandler {
    override fun openGithubIssue() = TODO()
//        linkHandler.openLink(context.resources.getString(Res.string.link_github_issue))

    override fun openIcons8() {
        TODO()
//        linkHandler.openLink(context.resources.getString(Res.string.link_icons8))
    }

    override fun openGithubRepository() {
        TODO()
//        linkHandler.openLink(context.resources.getString(Res.string.link_github_repository))
    }

    override fun openGithubReadme() = openGithubRepository()
}
