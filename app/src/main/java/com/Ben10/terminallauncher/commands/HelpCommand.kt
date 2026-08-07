package com.Ben10.terminallauncher.commands

/**
 * Lists the names of every command currently registered with the
 * CommandManager.
 *
 * The list of names is supplied lazily via [namesProvider] instead of
 * a fixed List, because CommandManager builds this command as part of
 * the very registry it needs to describe — a lazily-evaluated lambda
 * avoids a circular hard dependency between the two.
 */
class HelpCommand(private val namesProvider: () -> List<String>) : Command {

    override val name: String = "help"

    override fun execute(): CommandResult {
        return CommandResult.Output(listOf("Available commands:") + namesProvider())
    }
}
