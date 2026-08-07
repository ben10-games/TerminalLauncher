package com.Ben10.terminallauncher

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.Ben10.terminallauncher.commands.CommandManager
import com.Ben10.terminallauncher.commands.CommandResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

// Roasts fire on a random interval somewhere in this window, re-rolled
// after every roast.
private const val MinIntervalMillis = 5 * 60 * 1000L
private const val MaxIntervalMillis = 15 * 60 * 1000L

/**
 * Periodically triggers a random roast — reusing the existing "roast"
 * command via [CommandManager.triggerRoast] — while the launcher is
 * visible, on screen, and unlocked.
 *
 * Owns no [CoroutineScope] of its own: [start] is handed the scope to
 * launch into (the caller's lifecycle-scoped composable/coroutine
 * scope), so this class can never keep running past whatever started
 * it, and [stop] leaves nothing behind.
 */
class BackgroundRoastScheduler(
    private val context: Context,
    private val commandManager: CommandManager,
    private val onRoast: (List<String>) -> Unit
) {
    private var job: Job? = null

    /**
     * Starts the periodic loop on [scope]. Safe to call even if a loop
     * is already running — the previous one is stopped first, so there
     * is never more than one active at a time.
     */
    fun start(scope: CoroutineScope) {
        stop()
        job = scope.launch {
            while (isActive) {
                delay(randomIntervalMillis())
                if (isScreenAvailable()) {
                    val result = commandManager.triggerRoast()
                    if (result is CommandResult.Output) {
                        onRoast(result.lines)
                    }
                }
            }
        }
    }

    /** Cancels the running loop, if any. Safe to call when already stopped. */
    fun stop() {
        job?.cancel()
        job = null
    }

    private fun randomIntervalMillis(): Long =
        Random.nextLong(MinIntervalMillis, MaxIntervalMillis + 1)

    // Defensive re-check performed right before every roast fires. The
    // caller is only expected to run this scheduler while the launcher
    // is resumed, but a locked or screen-off device can still count as
    // "resumed" on some Android versions/OEM skins, so this is the
    // actual guarantee behind "don't run while locked or screen off."
    private fun isScreenAvailable(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isInteractive = powerManager?.isInteractive ?: false
        val isLocked = keyguardManager?.isKeyguardLocked ?: true
        return isInteractive && !isLocked
    }
}
