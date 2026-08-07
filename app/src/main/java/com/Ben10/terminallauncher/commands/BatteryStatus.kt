package com.Ben10.terminallauncher.commands

import android.content.Context
import android.os.BatteryManager

/** Coarse charging state, as reported by BatteryManager. */
enum class BatteryState { CHARGING, DISCHARGING, FULL, UNKNOWN }

/** A snapshot of the device's battery level and charging state. */
data class BatteryStatus(val level: Int, val state: BatteryState)

/**
 * Reads the device's current battery level and charging state directly
 * from the system's BatteryManager service — the modern, broadcast-free
 * way to query instantaneous battery properties.
 *
 * Shared by [BatteryCommand] (the "battery" command) and [RoastEngine]
 * (battery-based roast contexts), so this lookup logic exists in
 * exactly one place instead of being duplicated between them.
 *
 * Returns null if the platform can't currently report a real value —
 * callers decide how to handle that themselves.
 */
fun readBatteryStatus(context: Context): BatteryStatus? {
    return try {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            ?: return null

        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        // A negative capacity means the platform couldn't report a
        // real value, per BatteryManager's documented contract.
        if (level < 0) return null

        val status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        val state = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryState.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryState.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> BatteryState.FULL
            // Covers BATTERY_STATUS_NOT_CHARGING and BATTERY_STATUS_UNKNOWN.
            else -> BatteryState.UNKNOWN
        }

        BatteryStatus(level, state)
    } catch (_: Exception) {
        null
    }
}
