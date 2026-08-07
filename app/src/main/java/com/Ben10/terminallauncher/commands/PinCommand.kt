package com.Ben10.terminallauncher.commands

import android.content.Context

/**
 * Pins an installed app as a favorite: "pin <app name>".
 *
 * Reuses the same installed-app lookup as OpenCommand/SearchCommand
 * ([queryLaunchableApps]) rather than duplicating app discovery, and
 * delegates persistence to [FavoritesStore].
 */
class PinCommand(
    private val context: Context,
    private val favoritesStore: FavoritesStore
) : ArgumentAwareCommand {

    override val name: String = "pin"

    // Typing "pin" with nothing after it has nothing to pin.
    override fun execute(): CommandResult = executeWithArgs("")

    override fun executeWithArgs(args: String): CommandResult {
        val query = args.trim()
        if (query.isEmpty()) {
            return CommandResult.Output(listOf("Usage: pin <app name>"))
        }

        val matchedApp = queryLaunchableApps(context.packageManager).findByLabel(query)
            ?: return CommandResult.Output(listOf("App not found."))

        if (favoritesStore.contains(matchedApp.packageName)) {
            return CommandResult.Output(listOf("${matchedApp.label} is already pinned."))
        }

        favoritesStore.add(matchedApp.packageName)
        return CommandResult.Output(listOf("Pinned ${matchedApp.label}."))
    }
}
