package com.Ben10.terminallauncher.commands

import android.content.Context
import kotlinx.coroutines.delay

/**
 * Owns the set of commands the terminal understands and dispatches
 * raw user input to the matching command.
 *
 * To add a future command: implement [Command] in its own file in
 * this package, then add an instance of it to [commands] below.
 * No other file needs to change to support it.
 */
class CommandManager(context: Context) {

    // Owns the single TextToSpeech engine and all roast-selection logic
    // (context matching, priority, anti-repeat). Shared by the "roast"
    // command, the background scheduler, "open" (for the eight
    // recognized apps), and the unknown-command fallback below — see
    // RoastEngine.kt for why this lives in one place.
    private val roastEngine = RoastEngine(context)

    // Backs pin/unpin/favorites — held once here and shared across all
    // three commands so they always read and write the same persisted
    // set of favorites.
    private val favoritesStore = FavoritesStore(context)

    // Registry of all commands currently supported by the terminal.
    // Declaration order here is also the order "help" lists them in.
    private val commands: List<Command> = listOf(
        HelpCommand { commands.map { it.name } },
        ClearCommand(),
        TimeCommand(),
        DateCommand(),
        BatteryCommand(context),
        ForgotCommand(),
        OpenCommand(context, roastEngine),
        SearchCommand(context),
        PinCommand(context, favoritesStore),
        UnpinCommand(context, favoritesStore),
        FavoritesCommand(context, favoritesStore),
        RoastCommand(roastEngine)
    )

    /**
     * Looks up [rawInput] against the registry and executes the match.
     *
     * An exact, case-insensitive match against the full input is tried
     * first — this preserves existing behavior for no-argument commands
     * ("help", "clear", etc.) exactly as before. If nothing matches
     * exactly, and the input looks like "<name> <args>" for a command
     * that accepts arguments, that command's [ArgumentAwareCommand.executeWithArgs]
     * is called with the remaining text.
     *
     * If the matched command implements [DelayedCommand], this suspends
     * for its [DelayedCommand.delayMillis] before running it — every
     * other command runs immediately, unaffected.
     */
    suspend fun process(rawInput: String): CommandResult {
        val trimmed = rawInput.trim()

        val exactMatch = commands.firstOrNull { it.name.equals(trimmed, ignoreCase = true) }
        if (exactMatch != null) {
            if (exactMatch is DelayedCommand) {
                delay(exactMatch.delayMillis)
            }
            return exactMatch.execute()
        }

        for (command in commands) {
            if (command is ArgumentAwareCommand) {
                val prefix = "${command.name} "
                if (trimmed.startsWith(prefix, ignoreCase = true)) {
                    val args = trimmed.substring(prefix.length)
                    if (command is DelayedCommand) {
                        delay(command.delayMillis)
                    }
                    return command.executeWithArgs(args)
                }
            }
        }

        val unknownCommandRoast = roastEngine.roast(RoastContext.UNKNOWN_COMMAND).lines
        return CommandResult.Output(listOf("Unknown command.", "Type \"help\"") + unknownCommandRoast)
    }

    /**
     * Runs the roast engine directly for whatever ambient context
     * currently applies (battery/time/general) — the same as typing
     * "roast" — bypassing [process]'s text-lookup step. Used by
     * BackgroundRoastScheduler so scheduled roasts reuse the same
     * engine (and its single TextToSpeech instance) instead of a
     * second one being created.
     */
    fun triggerRoast(): CommandResult = roastEngine.roast()

    /**
     * Releases resources held by commands that need explicit cleanup
     * (currently just RoastEngine's TextToSpeech engine). Must be
     * called when the terminal screen is torn down, to avoid leaks.
     */
    fun shutdown() {
        roastEngine.shutdown()
    }
}
