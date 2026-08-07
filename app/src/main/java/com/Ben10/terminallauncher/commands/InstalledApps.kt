package com.Ben10.terminallauncher.commands

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build

/** A launchable app's display name and package, resolved from PackageManager. */
data class LaunchableApp(val label: String, val packageName: String)

/**
 * Queries every installed app that exposes a launcher entry point.
 *
 * Shared by any command that needs to look up installed apps (currently
 * "open" and "search"), so this lookup logic exists in exactly one place.
 */
fun queryLaunchableApps(packageManager: PackageManager): List<LaunchableApp> {
    val mainIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveInfos: List<ResolveInfo> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(mainIntent, 0)
        }

    return resolveInfos.map { resolveInfo ->
        LaunchableApp(
            label = resolveInfo.loadLabel(packageManager).toString(),
            packageName = resolveInfo.activityInfo.packageName
        )
    }
}

/**
 * Finds an app in this list by an exact, case-insensitive match on its
 * display label. Shared by every command that resolves a typed app
 * name to an installed app ("open", "pin", "unpin"), so this exact-
 * match logic exists in exactly one place instead of being duplicated
 * across them.
 */
fun List<LaunchableApp>.findByLabel(label: String): LaunchableApp? =
    firstOrNull { it.label.equals(label, ignoreCase = true) }
