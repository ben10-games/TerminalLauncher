package com.Ben10.terminallauncher.commands

import android.content.Context

/**
 * Removes an installed app from favorites: "unpin <app name>".
 *
 * Reuses the same installed-app lookup as OpenCommand/SearchCommand
 * ([queryLaunchableApps]) rather than duplicating app discovery, and
 * delegates persistence to [FavoritesStore].
 */
class UnpinCommand(
    private val context: Context,
    private val favoritesStore: FavoritesStore
) : ArgumentAwareCommand {

    override val name: String = "unpin"

    // Typing "unpin" with nothing after it has nothing to unpin.
    override fun execute(): CommandResult = executeWithArgs("")

    override fun executeWithArgs(args: String): CommandResult {
        val query = args.trim()
        if (query.isEmpty()) {
            return CommandResult.Output(listOf("Usage: unpin <app name>"))
        }

        val matchedApp = queryLaunchableApps(context.packageManager).findByLabel(query)
            ?: return CommandResult.Output(listOf("App not found."))

        if (!favoritesStore.contains(matchedApp.packageName)) {
            return CommandResult.Output(listOf("${matchedApp.label} is not pinned."))
        }

        favoritesStore.remove(matchedApp.packageName)
        return CommandResult.Output(listOf("Unpinned ${matchedApp.label}."))
    }
}
