package com.Ben10.terminallauncher.commands

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Displays the current local date, split across two lines: the
 * weekday name, then the month/day/year.
 *
 * Uses java.time (Kotlin's modern date API) rather than the legacy
 * java.util.Date/Calendar APIs.
 */
class DateCommand : Command {

    override val name: String = "date"

    private val weekdayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
    private val monthDayYearFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())

    override fun execute(): CommandResult {
        val today = LocalDate.now()
        val weekday = today.format(weekdayFormatter)
        val monthDayYear = today.format(monthDayYearFormatter)
        return CommandResult.Output(listOf("Current Date", weekday, monthDayYear))
    }
}
