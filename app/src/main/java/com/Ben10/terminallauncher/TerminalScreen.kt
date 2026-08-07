package com.Ben10.terminallauncher

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.Ben10.terminallauncher.commands.BatteryCommand
import com.Ben10.terminallauncher.commands.CommandManager
import com.Ben10.terminallauncher.commands.CommandResult
import com.Ben10.terminallauncher.commands.DateCommand
import com.Ben10.terminallauncher.commands.TimeCommand
import com.Ben10.terminallauncher.ui.theme.TerminalLauncherTheme
import kotlinx.coroutines.launch

// Terminal green, shared by every piece of text on this screen.
private val TerminalGreen = Color(0xFF00FF66)

// Shared text style so the title, history, prompt symbol, and typed
// input all look identical.
private val TerminalTextStyle = TextStyle(
    color = TerminalGreen,
    fontFamily = FontFamily.Monospace,
    fontSize = 18.sp
)

// Vertical gap used consistently between the title/history/prompt
// sections and between individual history lines, so nothing on screen
// touches edge-to-edge.
private val TerminalLineSpacing = 8.dp

// The greeting shown before the first prompt, and again whenever the
// terminal is cleared or reset — a single constant so all three call
// sites always show identical text.
private val WelcomeMessage = listOf("Welcome.", "Type \"help\"")

/**
 * Extracts a command's output lines for reuse outside the normal
 * command-submission flow (currently just the startup info panel).
 * Startup only ever runs commands that produce [CommandResult.Output],
 * so any other result type safely yields no lines.
 */
private fun outputLinesOf(result: CommandResult): List<String> =
    (result as? CommandResult.Output)?.lines ?: emptyList()

/**
 * TerminalHomeScreen
 *
 * Terminal-style home UI with command execution and a scrolling,
 * auto-following history of past prompts/output.
 *
 * Intentionally excluded per current requirements:
 * - No commands beyond the existing registered set (see the commands package)
 * - No launcher registration (not set as HOME yet)
 * - No buttons
 * - No custom/authored animations beyond BasicTextField's built-in cursor
 *
 * Layout:
 * A full-screen Box with a pure black background, containing a
 * top-to-bottom Column:
 *   - Fixed title line at the top.
 *   - A scrollable, auto-following history of every past "> command"
 *     line and its output, taking up all remaining vertical space.
 *   - The live prompt row ("> " + input) pinned at the bottom.
 */
@Composable
fun TerminalHomeScreen(modifier: Modifier = Modifier) {
    // Holds whatever the user has typed into the current, not-yet-
    // submitted line. Plain in-memory Compose state.
    var inputText by remember { mutableStateOf(TextFieldValue("")) }

    // Uses the application context (not the Activity context) so
    // anything built from it below never holds a reference to an
    // Activity across configuration changes.
    val context = LocalContext.current.applicationContext

    // Every previously submitted "> command" line and its resulting
    // output, in order. In-memory only — nothing is persisted. Cleared
    // entirely by the "clear" and "forgot" commands.
    //
    // Seeded once, at first composition, with the startup info panel:
    // a welcome message followed by the same time/date/battery output
    // the "time", "date", and "battery" commands themselves produce,
    // each separated by a blank line so the three sections read as
    // distinct groups rather than one undifferentiated block. Built by
    // calling those commands directly (not through CommandManager,
    // since this isn't a user-submitted "> ..." line) so the startup
    // panel can never drift from what those commands actually report.
    val history = remember {
        mutableStateListOf<String>().apply {
            addAll(WelcomeMessage)
            add("")
            addAll(outputLinesOf(TimeCommand().execute()))
            add("")
            addAll(outputLinesOf(DateCommand().execute()))
            add("")
            addAll(outputLinesOf(BatteryCommand(context).execute()))
        }
    }

    // Looks up and executes commands typed by the user. Created once
    // per screen instance; owns no UI state itself.
    val commandManager = remember { CommandManager(context) }

    // Stores submitted commands (deduplicated consecutively) for
    // Up/Down hardware-key recall. Separate from `history` above, which
    // is the visible transcript, not a recall list.
    val commandHistory = remember { CommandHistory() }

    // Reuses the existing "roast" command (and its single TextToSpeech
    // engine, via CommandManager.triggerRoast()) to speak and print a
    // random roast on an unattended timer. Appends its lines directly
    // to `history`, the same visible transcript everything else writes
    // to — no "> " prefix, since this wasn't user-submitted.
    val backgroundRoastScheduler = remember {
        BackgroundRoastScheduler(context, commandManager) { lines ->
            history.addAll(lines)
        }
    }

    // Releases CommandManager's resources (currently RoastCommand's
    // TextToSpeech engine) when this composable leaves composition,
    // so nothing is leaked when the terminal screen is torn down.
    DisposableEffect(commandManager) {
        onDispose { commandManager.shutdown() }
    }

    // Lets us programmatically request focus on the input field when
    // the user taps anywhere on the prompt row.
    val focusRequester = remember { FocusRequester() }

    // CommandManager.process is a suspend function (some commands, like
    // "roast", apply a short delay before running), so submitting a
    // command needs a coroutine to call it from.
    val coroutineScope = rememberCoroutineScope()

    // Starts the background roast scheduler whenever this screen becomes
    // the active, resumed one, and stops it the instant it isn't —
    // covering "launcher no longer visible" and "activity paused/stopped"
    // in one place. onDispose (app closed, or this composable torn down)
    // stops it too, as a backstop even if no ON_PAUSE was observed first.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backgroundRoastScheduler) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> backgroundRoastScheduler.start(coroutineScope)
                Lifecycle.Event.ON_PAUSE -> backgroundRoastScheduler.stop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            backgroundRoastScheduler.stop()
        }
    }

    // Drives the history's scroll position, so it can be auto-scrolled
    // to the newest line whenever history grows.
    val historyScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            // Reserves space for the on-screen keyboard using the actual,
            // live IME inset instead of a hardcoded height — this is what
            // makes the layout resize instead of being covered, since
            // enableEdgeToEdge() means the system no longer resizes the
            // window for us via windowSoftInputMode.
            .imePadding()
            .padding(16.dp) // small edge margin, purely cosmetic
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TerminalHeader()

            TerminalHistoryList(
                history = history,
                scrollState = historyScrollState,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = TerminalLineSpacing)
            )

            TerminalPromptRow(
                inputText = inputText,
                onInputChange = { inputText = it },
                focusRequester = focusRequester,
                onHistoryUp = {
                    commandHistory.previous()?.let { recalled ->
                        inputText = TextFieldValue(recalled, TextRange(recalled.length))
                    }
                },
                onHistoryDown = {
                    commandHistory.next()?.let { recalled ->
                        inputText = TextFieldValue(recalled, TextRange(recalled.length))
                    }
                },
                onSubmit = {
                    val submitted = inputText.text

                    // Always reset the input line immediately on submit,
                    // whether or not a command matched — don't wait for
                    // an in-flight delayed command to finish first.
                    inputText = TextFieldValue("")

                    // Ignore empty submissions — nothing to run,
                    // nothing to record.
                    if (submitted.isNotBlank()) {
                        commandHistory.record(submitted)
                        coroutineScope.launch {
                            when (val result = commandManager.process(submitted)) {
                                is CommandResult.Output -> {
                                    history.add("> $submitted")
                                    history.addAll(result.lines)
                                }
                                CommandResult.ClearScreen -> {
                                    history.clear()
                                    history.addAll(WelcomeMessage)
                                }
                                CommandResult.ResetTerminal -> {
                                    history.clear()
                                    history.addAll(WelcomeMessage)
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

/** The fixed title line at the top of the terminal. */
@Composable
private fun TerminalHeader() {
    Text(text = "Terminal Launcher v1", style = TerminalTextStyle)
}

/** Animates [scrollState] to its current bottom-most scroll position. */
private suspend fun scrollToBottom(scrollState: ScrollState) {
    scrollState.animateScrollTo(scrollState.maxValue)
}

/**
 * The scrollable history of past prompts/output. Automatically scrolls
 * to the newest line whenever [history] grows, so output never has to
 * be scrolled to manually — matching real terminal behavior.
 */
@Composable
private fun TerminalHistoryList(
    history: List<String>,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            scrollToBottom(scrollState)
        }
    }

    // Also re-scroll to the bottom whenever the keyboard's visible
    // height changes (opening, closing, or animating), so the last
    // line stays revealed above the keyboard even when no new history
    // line was just added.
    val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(imeBottomPx) {
        if (imeBottomPx > 0) {
            scrollToBottom(scrollState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(TerminalLineSpacing)
    ) {
        history.forEach { line ->
            Text(text = line, style = TerminalTextStyle)
        }
    }
}

/**
 * The live prompt row: a static "> " symbol followed by an editable
 * input field, styled to avoid any resemblance to a standard Android
 * text field — no underline, no fill, and terminal-green text
 * selection instead of the default system blue.
 */
@Composable
private fun TerminalPromptRow(
    inputText: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester,
    onHistoryUp: () -> Unit,
    onHistoryDown: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = TerminalLineSpacing)
            // Tapping anywhere on this row opens the keyboard, not
            // just tapping precisely on the text cursor.
            .clickable { focusRequester.requestFocus() }
    ) {
        // Static prompt symbol.
        Text(text = "> ", style = TerminalTextStyle)

        val terminalSelectionColors = TextSelectionColors(
            handleColor = TerminalGreen,
            backgroundColor = TerminalGreen.copy(alpha = 0.4f)
        )

        CompositionLocalProvider(LocalTextSelectionColors provides terminalSelectionColors) {
            BasicTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    // Hardware/Bluetooth keyboards dispatch DirectionUp/
                    // DirectionDown as real key events; on-screen keyboards
                    // don't, so this only ever engages when a hardware
                    // keyboard is available, per the requirement.
                    .onPreviewKeyEvent { keyEvent ->
                        if (keyEvent.type != KeyEventType.KeyDown) {
                            false
                        } else {
                            when (keyEvent.key) {
                                Key.DirectionUp -> {
                                    onHistoryUp()
                                    true
                                }
                                Key.DirectionDown -> {
                                    onHistoryDown()
                                    true
                                }
                                else -> false
                            }
                        }
                    },
                textStyle = TerminalTextStyle,
                singleLine = true,
                cursorBrush = SolidColor(TerminalGreen),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() })
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TerminalHomeScreenPreview() {
    TerminalLauncherTheme {
        TerminalHomeScreen()
    }
}
