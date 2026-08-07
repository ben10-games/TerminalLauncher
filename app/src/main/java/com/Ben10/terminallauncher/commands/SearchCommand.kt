package com.Ben10.terminallauncher.commands

import android.content.Context

/**
 * Searches installed launchable apps by partial, case-insensitive name
 * match: "search <text>".
 *
 * Reuses the shared [queryLaunchableApps] lookup (also used by
 * [OpenCommand]) rather than duplicating app-discovery logic.
 */
class SearchCommand(private val context: Context) : ArgumentAwareCommand {

    override val name: String = "search"

    // Typing "search" with nothing after it has nothing to match against.
    override fun execute(): CommandResult = executeWithArgs("")

    override fun executeWithArgs(args: String): CommandResult {
        val query = args.trim()
        if (query.isEmpty()) {
            return CommandResult.Output(listOf("Usage: search <app name>"))
        }

        val matches = queryLaunchableApps(context.packageManager)
            .filter { it.label.contains(query, ignoreCase = true) }
            .map { it.label }
            .distinct()
            .sorted()
            .take(10)

        if (matches.isEmpty()) {
            return CommandResult.Output(listOf("No matching apps found."))
        }

        val lines = mutableListOf("Found ${matches.size} apps:")
        lines.addAll(matches)
        return CommandResult.Output(lines)
    }
}
