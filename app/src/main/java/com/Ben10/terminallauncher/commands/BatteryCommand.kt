package com.Ben10.terminallauncher.commands

import android.content.Context

/**
 * Displays the device's current battery level and charging state.
 *
 * Reads via [readBatteryStatus], the lookup shared with [RoastEngine]'s
 * battery-based roast contexts, rather than querying BatteryManager
 * itself.
 */
class BatteryCommand(private val context: Context) : Command {

    override val name: String = "battery"

    override fun execute(): CommandResult {
        val status = readBatteryStatus(context) ?: return unreadableBatteryInfo()

        val stateText = when (status.state) {
            BatteryState.CHARGING -> "Charging"
            BatteryState.DISCHARGING -> "Discharging"
            BatteryState.FULL -> "Full"
            BatteryState.UNKNOWN -> "Unknown"
        }

        return CommandResult.Output(
            listOf(
                "Battery Status",
                "Level: ${status.level}%",
                "State: $stateText"
            )
        )
    }

    private fun unreadableBatteryInfo(): CommandResult.Output {
        return CommandResult.Output(listOf("Battery Status", "Unable to read battery information."))
    }
}
