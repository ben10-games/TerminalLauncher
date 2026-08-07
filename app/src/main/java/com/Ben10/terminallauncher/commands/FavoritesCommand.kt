package com.Ben10.terminallauncher.commands

import android.content.Context

/**
 * Lists all currently pinned apps: "favorites".
 *
 * Resolves each pinned package name's current display label via
 * [queryLaunchableApps] rather than storing labels, so listings never
 * go stale and apps that are no longer installed are silently skipped
 * instead of showing broken entries.
 */
class FavoritesCommand(
    private val context: Context,
    private val favoritesStore: FavoritesStore
) : Command {

    override val name: String = "favorites"

    override fun execute(): CommandResult {
        val pinnedPackageNames = favoritesStore.getAll()
        if (pinnedPackageNames.isEmpty()) {
            return noFavorites()
        }

        val labels = queryLaunchableApps(context.packageManager)
            .filter { it.packageName in pinnedPackageNames }
            .map { it.label }
            .distinct()
            .sorted()

        if (labels.isEmpty()) {
            return noFavorites()
        }

        return CommandResult.Output(listOf("Favorites:") + labels)
    }

    private fun noFavorites(): CommandResult.Output =
        CommandResult.Output(listOf("No favorites yet.", "Use \"pin <app>\" to add one."))
}
