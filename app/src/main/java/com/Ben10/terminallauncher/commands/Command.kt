package com.Ben10.terminallauncher.commands

/**
 * Represents everything a Command can ask the terminal to do.
 *
 * - [Output]: append one or more lines of text to the terminal history.
 * - [ClearScreen]: wipe the terminal's entire history.
 * - [ResetTerminal]: restore the terminal to its startup state. Kept
 *   separate from [ClearScreen] (even though both currently produce
 *   the same visible "Welcome." / "Type \"help\"" screen) so recovery
 *   behavior can diverge from a plain clear in the future without
 *   touching ClearScreen or the "clear" command.
 *
 * This is a sealed class (rather than a plain String return) so that
 * every caller handling a CommandResult is forced to consider every
 * possible outcome, and so new outcomes can be added later — e.g. a
 * future command that needs to do something other than print text —
 * without changing the signature of existing commands.
 */
sealed class CommandResult {
    data class Output(val lines: List<String>) : CommandResult()
    object ClearScreen : CommandResult()
    object ResetTerminal : CommandResult()
}

/**
 * Contract for a single terminal command.
 *
 * Every command has a unique, lowercase [name] — what the user types
 * to invoke it — and produces a [CommandResult] when executed.
 *
 * Commands do not take arguments yet and know nothing about the UI,
 * focus, or terminal history; they only describe what should happen.
 * To add a new command in the future: create a class implementing
 * this interface in this package, then register it in CommandManager.
 */
interface Command {
    val name: String
    fun execute(): CommandResult
}

/**
 * A [Command] that also accepts arguments typed after its name
 * (e.g. "open chrome" — "open" is the name, "chrome" is the argument).
 *
 * Most commands take no arguments and only need [Command.execute].
 * A command that needs arguments implements this instead; CommandManager
 * routes any text following the command's name to [executeWithArgs]
 * rather than [Command.execute]. Existing no-argument commands are
 * unaffected — this interface is purely additive.
 */
interface ArgumentAwareCommand : Command {
    fun executeWithArgs(args: String): CommandResult
}

/**
 * A [Command] that should be preceded by a short delay before it runs,
 * to convey that the terminal is "thinking" (e.g. "roast").
 *
 * CommandManager applies [delayMillis] itself before calling
 * [Command.execute] (or [ArgumentAwareCommand.executeWithArgs]) on a
 * command that implements this — the command's own execute logic
 * doesn't need to know it was delayed. Commands that don't implement
 * this run immediately, exactly as before.
 */
interface DelayedCommand : Command {
    val delayMillis: Long
}
