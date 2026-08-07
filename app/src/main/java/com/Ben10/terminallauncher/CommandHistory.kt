package com.Ben10.terminallauncher

/**
 * Stores previously submitted terminal commands and supports Up/Down-style
 * recall through them, the way a shell's command history works.
 *
 * This is deliberately separate from the terminal's visible transcript
 * (which also contains command output and the startup info panel) —
 * CommandHistory only ever holds the raw command strings a user actually
 * typed and submitted, purely for arrow-key recall. It has no Android or
 * Compose dependency and knows nothing about how commands are executed.
 */
class CommandHistory {

    // Unique-consecutive submitted commands, oldest first.
    private val entries = mutableListOf<String>()

    // Position within entries while navigating with previous()/next().
    // A value equal to entries.size means "not currently navigating" —
    // i.e. back at the live, not-yet-submitted line.
    private var cursor = 0

    /**
     * Records [command] as a new history entry, unless it's blank or
     * identical to the most recently recorded command (so repeated
     * submissions like "battery", "battery", "battery" collapse into a
     * single entry). Always resets navigation back to the live line
     * afterward, matching real terminal behavior.
     */
    fun record(command: String) {
        val trimmed = command.trim()
        if (trimmed.isEmpty()) return
        if (entries.lastOrNull() != trimmed) {
            entries.add(trimmed)
        }
        cursor = entries.size
    }

    /**
     * Moves one step back to an older command and returns it, or returns
     * null if there is no older command to move to (already at the
     * oldest entry, or history is empty).
     */
    fun previous(): String? {
        if (cursor == 0) return null
        cursor--
        return entries[cursor]
    }

    /**
     * Moves one step forward toward the newest command and returns it.
     * Moving forward past the newest recorded entry returns an empty
     * string (the live line), matching shell behavior. Returns null if
     * already at the live line (nothing to move forward from).
     */
    fun next(): String? {
        if (cursor >= entries.size) return null
        cursor++
        return if (cursor == entries.size) "" else entries[cursor]
    }
}
