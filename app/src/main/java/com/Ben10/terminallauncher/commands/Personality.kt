package com.Ben10.terminallauncher.commands

/**
 * The tone a roast is delivered in. Every roast in [RoastPools] belongs
 * to exactly one personality — see [RoastEngine] for how personality
 * variety (never the same one more than three times in a row) is
 * enforced at pick time.
 */
enum class Personality {
    SARCASTIC,
    SAVAGE,
    FRIENDLY,
    DEADPAN,
    NERD,
    MOTIVATIONAL
}

/** A single roast line and the personality it's written in. */
data class Roast(val text: String, val personality: Personality)
