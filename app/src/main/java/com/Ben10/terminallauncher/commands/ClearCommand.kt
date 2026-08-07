package com.Ben10.terminallauncher.commands

/**
 * Clears the terminal's entire history.
 *
 * This command doesn't touch any UI state itself — it only signals
 * intent via [CommandResult.ClearScreen]. The caller (the screen
 * holding the history) decides how to act on that signal.
 */
class ClearCommand : Command {

    override val name: String = "clear"

    override fun execute(): CommandResult = CommandResult.ClearScreen
}
