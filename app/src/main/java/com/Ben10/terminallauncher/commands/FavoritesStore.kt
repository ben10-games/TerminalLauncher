package com.Ben10.terminallauncher.commands

import android.content.Context

private const val PreferencesName = "favorites"
private const val FavoritesKey = "pinned_package_names"

/**
 * Persists the set of pinned apps' package names using SharedPreferences,
 * so favorites survive app restarts.
 *
 * Stores package names only — never display labels. Labels are always
 * resolved fresh via [queryLaunchableApps] wherever favorites are shown,
 * so this store can never go stale if an app's display name changes,
 * and has nothing to say about apps that are no longer installed.
 */
class FavoritesStore(context: Context) {

    private val preferences = context.applicationContext
        .getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)

    /** All currently pinned package names, in no particular order. */
    fun getAll(): Set<String> =
        preferences.getStringSet(FavoritesKey, emptySet()) ?: emptySet()

    /** Returns true if [packageName] is currently pinned. */
    fun contains(packageName: String): Boolean = packageName in getAll()

    /** Pins [packageName]. No-op if already pinned. */
    fun add(packageName: String) {
        val updated = getAll() + packageName
        preferences.edit().putStringSet(FavoritesKey, updated).apply()
    }

    /** Unpins [packageName]. No-op if not currently pinned. */
    fun remove(packageName: String) {
        val updated = getAll() - packageName
        preferences.edit().putStringSet(FavoritesKey, updated).apply()
    }
}
