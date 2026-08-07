package com.Ben10.terminallauncher.commands

/**
 * Resets the terminal back to its startup state.
 *
 * This command doesn't touch any UI state itself — it only signals
 * intent via [CommandResult.ResetTerminal]. The caller (the screen
 * holding the history) decides how to act on that signal.
 */
class ForgotCommand : Command {

    override val name: String = "forgot"

    override fun execute(): CommandResult = CommandResult.ResetTerminal
}
