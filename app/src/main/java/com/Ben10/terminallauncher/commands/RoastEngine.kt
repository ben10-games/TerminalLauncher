package com.Ben10.terminallauncher.commands

import android.content.Context
import android.speech.tts.TextToSpeech
import java.time.LocalTime
import java.util.Locale

/**
 * Every situation the roast engine can specifically react to, ranked by
 * [priority] — when more than one context matches at once (e.g. battery
 * is low AND it's night), the highest-priority match wins.
 *
 * To add a future context: add an entry here with an appropriate
 * priority, add its pool to [RoastPools], and decide where in
 * [RoastEngine] it gets matched (an ambient signal like battery/time,
 * or an explicit context passed in by a caller like [OPENING_YOUTUBE]).
 */
enum class RoastContext(val priority: Int) {
    // Direct reactions to something the user just did — highest tier,
    // since these are the most specific thing the engine can know.
    OPENING_YOUTUBE(100),
    OPENING_TIKTOK(100),
    OPENING_INSTAGRAM(100),
    OPENING_CHROME(100),
    OPENING_SETTINGS(100),
    OPENING_CALCULATOR(100),
    OPENING_CAMERA(100),
    OPENING_SPOTIFY(100),

    UNKNOWN_COMMAND(90),

    // Ambient battery signals — checked on every roast, regardless of
    // trigger. Ranked by how attention-grabbing each state is.
    BATTERY_UNDER_15(80),
    BATTERY_CHARGING(60),
    BATTERY_FULL(50),

    // Ambient time-of-day — always matches exactly one of these, so
    // this is effectively the "default" tier whenever nothing more
    // specific applies.
    MORNING(20),
    AFTERNOON(20),
    NIGHT(20),

    // True fallback — only reached if literally nothing else matched
    // (e.g. battery status couldn't be read). Always present so a
    // roast can never fail to have a context.
    GENERAL(0)
}

// Maps a successfully opened app's display label (matched the same,
// case-insensitive way OpenCommand already matches it) to the roast
// context it should trigger. Only these eight apps get a reaction;
// opening anything else behaves exactly as before.
private val AppOpenContextsByLabel: Map<String, RoastContext> = mapOf(
    "youtube" to RoastContext.OPENING_YOUTUBE,
    "tiktok" to RoastContext.OPENING_TIKTOK,
    "instagram" to RoastContext.OPENING_INSTAGRAM,
    "chrome" to RoastContext.OPENING_CHROME,
    "settings" to RoastContext.OPENING_SETTINGS,
    "calculator" to RoastContext.OPENING_CALCULATOR,
    "camera" to RoastContext.OPENING_CAMERA,
    "spotify" to RoastContext.OPENING_SPOTIFY
)

/**
 * Looks up the [RoastContext] for a just-opened app's display label,
 * if it's one of the eight recognized ones. Used by [OpenCommand].
 */
fun roastContextForOpenedApp(label: String): RoastContext? =
    AppOpenContextsByLabel[label.trim().lowercase(Locale.getDefault())]

/**
 * Centralized, rule-based roast engine. Everything about *what* to say
 * and *when* lives here, in one place — [RoastCommand] (typed "roast"),
 * the background scheduler (via [CommandManager.triggerRoast]),
 * [OpenCommand] (for the eight recognized apps), and CommandManager's
 * unknown-command fallback all go through this same engine instead of
 * each inventing their own roast text or speaking logic.
 *
 * Owns the single TextToSpeech engine every roast is spoken through —
 * the one and only TextToSpeech instance in the app.
 */
class RoastEngine(context: Context) {

    private val appContext = context.applicationContext

    @Volatile
    private var isTtsReady = false

    // Initialized once, safely: if the device has no TTS engine or
    // initialization otherwise fails, isTtsReady simply stays false
    // and roast() falls back to text-only output.
    private val textToSpeech: TextToSpeech = TextToSpeech(appContext) { status ->
        isTtsReady = status == TextToSpeech.SUCCESS
        if (isTtsReady) {
            applyLocaleSafely()
        }
    }

    private fun applyLocaleSafely() {
        val result = textToSpeech.setLanguage(Locale.getDefault())
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            isTtsReady = false
        }
    }

    // The most recently spoken roast's text, so the exact same line is
    // never picked twice in a row, regardless of which context it
    // comes from.
    @Volatile
    private var lastRoastText: String? = null

    // Tracks the personality of the most recently spoken roast, and how
    // many times in a row it's been used, so no personality is picked
    // more than three times consecutively (see pickRoast). Tracked
    // globally, across every trigger source, same as lastRoastText.
    @Volatile
    private var lastPersonality: Personality? = null

    @Volatile
    private var personalityStreak: Int = 0

    /**
     * Picks and speaks a roast for the strongest currently-matched
     * context, then returns it as terminal output.
     *
     * [explicitContext], if given (an app was just opened, or the user
     * typed something unrecognized), is added to whatever ambient
     * contexts (battery, time of day) also currently match; the
     * highest-[RoastContext.priority] match wins. With no
     * [explicitContext], only ambient contexts and [RoastContext.GENERAL]
     * are considered — this is what a plain "roast" or a background
     * timer tick uses.
     */
    fun roast(explicitContext: RoastContext? = null): CommandResult.Output {
        val matched = buildSet {
            explicitContext?.let { add(it) }
            addAll(batteryContexts())
            add(timeOfDayContext())
            add(RoastContext.GENERAL)
        }

        val strongest = matched.maxByOrNull { it.priority } ?: RoastContext.GENERAL
        val roast = pickRoast(RoastPools.forContext(strongest))

        speak(roast.text)
        return CommandResult.Output(listOf(roast.text))
    }

    private fun pickRoast(pool: List<Roast>): Roast {
        // If the last three roasts in a row shared a personality, that
        // personality is excluded from this pick — guaranteeing no
        // personality ever runs more than three times consecutively.
        // Falls back to the full pool if that would leave nothing to
        // choose from (a context whose pool is all one personality).
        val blockedPersonality = lastPersonality.takeIf { personalityStreak >= 3 }
        val eligibleByPersonality = pool
            .filterNot { it.personality == blockedPersonality }
            .ifEmpty { pool }

        // Within whatever's still eligible, excludes the exact last
        // line spoken so it's never immediately repeated; falls back
        // the same way if that would leave nothing to choose from.
        val candidates = eligibleByPersonality
            .filterNot { it.text == lastRoastText }
            .ifEmpty { eligibleByPersonality }

        val chosen = candidates.random()

        lastRoastText = chosen.text
        personalityStreak = if (chosen.personality == lastPersonality) personalityStreak + 1 else 1
        lastPersonality = chosen.personality

        return chosen
    }

    private fun speak(text: String) {
        if (isTtsReady) {
            try {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "roast_utterance")
            } catch (_: Exception) {
                // Speaking failed for any reason — the roast still
                // displays in the terminal regardless.
            }
        }
    }

    private fun batteryContexts(): Set<RoastContext> {
        val status = readBatteryStatus(appContext) ?: return emptySet()
        return buildSet {
            if (status.level in 0..14) add(RoastContext.BATTERY_UNDER_15)
            if (status.state == BatteryState.CHARGING) add(RoastContext.BATTERY_CHARGING)
            if (status.state == BatteryState.FULL || status.level >= 100) add(RoastContext.BATTERY_FULL)
        }
    }

    private fun timeOfDayContext(): RoastContext =
        when (LocalTime.now().hour) {
            in 5..11 -> RoastContext.MORNING
            in 12..17 -> RoastContext.AFTERNOON
            else -> RoastContext.NIGHT
        }

    /**
     * Releases the TextToSpeech engine. Must be called when the
     * terminal screen is torn down, to avoid leaking it.
     */
    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
