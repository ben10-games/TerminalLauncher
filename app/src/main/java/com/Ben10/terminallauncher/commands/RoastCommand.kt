package com.Ben10.terminallauncher.commands

/**
 * Speaks and displays a roast for whichever context currently applies
 * (battery, time of day, or general): "roast".
 *
 * All roast text, TextToSpeech handling, and context matching live in
 * [RoastEngine] — this command is just the manual trigger for it, and
 * uses the exact same engine (and the exact same TextToSpeech engine)
 * as the background scheduler, "open", and the unknown-command
 * fallback.
 *
 * Implements [DelayedCommand] so CommandManager waits ~1 second before
 * calling [execute] — purely a "thinking" pause, unrelated to roast
 * selection itself.
 */
class RoastCommand(private val roastEngine: RoastEngine) : DelayedCommand {

    override val name: String = "roast"

    override val delayMillis: Long = 1000L

    override fun execute(): CommandResult = roastEngine.roast()
}
