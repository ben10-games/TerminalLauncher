package com.Ben10.terminallauncher.commands

import android.content.Context
import android.content.Intent

/**
 * Launches an installed app by name: "open <app name>".
 *
 * Implements [ArgumentAwareCommand] rather than just [Command], since
 * it needs the text typed after "open" to know which app to launch.
 *
 * After a successful launch, checks [roastContextForOpenedApp] for the
 * app's display label — if it's one of the eight recognized apps, a
 * contextual roast (via the shared [RoastEngine]) is appended to the
 * output and spoken. Opening any other app is unaffected.
 */
class OpenCommand(
    private val context: Context,
    private val roastEngine: RoastEngine
) : ArgumentAwareCommand {

    override val name: String = "open"

    // Typing "open" with nothing after it is treated as an empty query.
    override fun execute(): CommandResult = executeWithArgs("")

    override fun executeWithArgs(args: String): CommandResult {
        val query = args.trim()
        if (query.isEmpty()) {
            return CommandResult.Output(listOf("Usage: open <app name>"))
        }

        val packageManager = context.packageManager
        val launchableApps = queryLaunchableApps(packageManager)

        // Exact, case-insensitive match on the app's display name.
        val matchedApp = launchableApps.findByLabel(query)

        if (matchedApp != null) {
            val launchIntent = packageManager.getLaunchIntentForPackage(matchedApp.packageName)
            if (launchIntent != null) {
                // Required when starting an Activity from a non-Activity
                // Context (we're holding the application context).
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)

                val lines = mutableListOf("Opening ${matchedApp.label}...")
                roastContextForOpenedApp(matchedApp.label)?.let { roastContext ->
                    lines.addAll(roastEngine.roast(roastContext).lines)
                }
                return CommandResult.Output(lines)
            }
            return CommandResult.Output(listOf("App not found."))
        }

        // No exact match — suggest up to 5 installed apps whose name
        // contains what was typed, alphabetically.
        val suggestions = launchableApps
            .filter { it.label.contains(query, ignoreCase = true) }
            .map { it.label }
            .distinct()
            .sorted()
            .take(5)

        val lines = mutableListOf("App not found.")
        if (suggestions.isNotEmpty()) {
            lines.add("Did you mean:")
            lines.addAll(suggestions)
        }
        return CommandResult.Output(lines)
    }
}
