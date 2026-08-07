package com.Ben10.terminallauncher.commands

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Displays the current local device time.
 *
 * Uses java.time (Kotlin's modern date/time API) rather than the
 * legacy java.util.Date/Calendar APIs. The formatter takes an explicit
 * Locale (matching DateCommand's formatters) rather than relying on
 * the platform default implicitly.
 */
class TimeCommand : Command {

    override val name: String = "time"

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())

    override fun execute(): CommandResult {
        val currentTime = LocalTime.now().format(formatter)
        return CommandResult.Output(listOf("Current Time", currentTime))
    }
}
