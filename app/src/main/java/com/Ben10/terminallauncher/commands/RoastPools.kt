package com.Ben10.terminallauncher.commands

/**
 * The actual roast database: one pool of [Roast]s per [RoastContext].
 * Kept in its own file so this (long) content doesn't crowd out
 * [RoastEngine]'s actual picking/priority logic.
 *
 * Every roast is tagged with the [Personality] it's written in — see
 * [RoastEngine.pickRoast] for how that tag is used to keep the same
 * tone from repeating too many times in a row.
 *
 * To add a future context: give it a case in [forContext] returning a
 * new pool below. The compiler enforces this — [RoastEngine] matches
 * exhaustively over [RoastContext], so a context without a pool here
 * won't compile.
 */
object RoastPools {

    fun forContext(context: RoastContext): List<Roast> = when (context) {
        RoastContext.OPENING_YOUTUBE -> youtube
        RoastContext.OPENING_TIKTOK -> tiktok
        RoastContext.OPENING_INSTAGRAM -> instagram
        RoastContext.OPENING_CHROME -> chrome
        RoastContext.OPENING_SETTINGS -> settings
        RoastContext.OPENING_CALCULATOR -> calculator
        RoastContext.OPENING_CAMERA -> camera
        RoastContext.OPENING_SPOTIFY -> spotify
        RoastContext.UNKNOWN_COMMAND -> unknownCommand
        RoastContext.BATTERY_UNDER_15 -> batteryUnder15
        RoastContext.BATTERY_CHARGING -> batteryCharging
        RoastContext.BATTERY_FULL -> batteryFull
        RoastContext.MORNING -> morning
        RoastContext.AFTERNOON -> afternoon
        RoastContext.NIGHT -> night
        RoastContext.GENERAL -> general
    }

    private val youtube = listOf(
        Roast("You've opened YouTube again. Commitment is admirable.", Personality.SARCASTIC),
        Roast("One more video, right? That's what you said an hour ago.", Personality.FRIENDLY),
        Roast("Your watch history says more about you than your search history.", Personality.DEADPAN),
        Roast("Autoplay is the only thing making decisions for you right now.", Personality.SAVAGE),
        Roast("You clicked YouTube like it was going to say something new.", Personality.SARCASTIC),
        Roast("Your recommended feed has better pattern recognition than most machine learning models, and it's trained entirely on you.", Personality.NERD),
        Roast("You're basically a certified YouTube historian by now — keep going, the algorithm believes in you.", Personality.MOTIVATIONAL),
        Roast("Hey, no judgment. The next video really might be the good one.", Personality.FRIENDLY),
        Roast("You didn't come here for anything specific. You never do.", Personality.SAVAGE)
    )

    private val tiktok = listOf(
        Roast("Fifteen seconds turned into fifteen minutes again, didn't it?", Personality.DEADPAN),
        Roast("Your thumb has more stamina than the rest of you.", Personality.SAVAGE),
        Roast("The algorithm knows you better than your friends do.", Personality.SARCASTIC),
        Roast("You opened TikTok \"just to check something.\" Sure you did.", Personality.SARCASTIC),
        Roast("Somewhere, your For You page is taking notes on your life choices.", Personality.DEADPAN),
        Roast("That's the third time this hour the app pulled you back in. Impressive persistence, yours or its.", Personality.FRIENDLY),
        Roast("The recommendation engine optimized for your attention span in under a second, faster than most teams ship a hotfix.", Personality.NERD),
        Roast("You've got this. Somewhere around video eighty, greatness awaits.", Personality.MOTIVATIONAL),
        Roast("You didn't open TikTok. TikTok opened you.", Personality.SAVAGE)
    )

    private val instagram = listOf(
        Roast("Checking if anyone liked your story from three hours ago?", Personality.SARCASTIC),
        Roast("Your feed has more filters than your actual honesty.", Personality.SAVAGE),
        Roast("You double-tapped a photo you'll forget in ten seconds.", Personality.DEADPAN),
        Roast("Comparing your life to someone's highlight reel again?", Personality.FRIENDLY),
        Roast("Stories fade in 24 hours. Your scrolling habit hasn't.", Personality.SARCASTIC),
        Roast("Every refresh is a fresh roll of the dopamine dice, and the house always wins.", Personality.NERD),
        Roast("You're doing great. Somewhere out there, someone's life looks worse than their photos suggest too.", Personality.MOTIVATIONAL),
        Roast("You went to check one notification and stayed for twenty minutes. Standard procedure.", Personality.DEADPAN),
        Roast("That's not research, that's stalking with extra steps.", Personality.SAVAGE)
    )

    private val chrome = listOf(
        Roast("Seventeen tabs open and you're opening an eighteenth.", Personality.DEADPAN),
        Roast("You typed \"quick search\" and meant \"forty-five minutes.\"", Personality.SARCASTIC),
        Roast("Your browser history is a documentary no one asked for.", Personality.SAVAGE),
        Roast("Another tab you'll never close, added to the pile.", Personality.DEADPAN),
        Roast("Ctrl+T is basically your reflex at this point.", Personality.NERD),
        Roast("Hey, at least you're curious. That's worth something, probably.", Personality.FRIENDLY),
        Roast("Somewhere in that tab graveyard is a memory limit begging for mercy.", Personality.NERD),
        Roast("Look at you, researching. Very productive, if this were 2011.", Personality.SARCASTIC),
        Roast("You'll bookmark it. You will never open the bookmark.", Personality.SAVAGE)
    )

    private val settings = listOf(
        Roast("You opened Settings again. Hoping it'll fix itself?", Personality.SARCASTIC),
        Roast("You keep opening Settings like there's a secret level.", Personality.FRIENDLY),
        Roast("Toggling the same switch back and forth isn't troubleshooting.", Personality.DEADPAN),
        Roast("Nothing's broken. You're just poking around, admit it.", Personality.SAVAGE),
        Roast("Settings won't fix what a good night's sleep would.", Personality.DEADPAN),
        Roast("You're one menu deep into a problem that doesn't exist. Bold exploration.", Personality.SARCASTIC),
        Roast("Every setting you touch here has a ninety percent chance of being reverted within the hour. That's not a bug, that's you.", Personality.NERD),
        Roast("You've got this. The perfect configuration is only forty more toggles away.", Personality.MOTIVATIONAL),
        Roast("This isn't maintenance. This is procrastination with a gear icon.", Personality.SAVAGE)
    )

    private val calculator = listOf(
        Roast("Even your calculator app knows you're procrastinating on real math.", Personality.SARCASTIC),
        Roast("I've seen calculators with a better social life.", Personality.SAVAGE),
        Roast("Typing 1 plus 1 into a calculator app takes real confidence.", Personality.SARCASTIC),
        Roast("You opened this for one number and forgot what it was.", Personality.DEADPAN),
        Roast("Your mental math retired the day you installed this app.", Personality.DEADPAN),
        Roast("Hey, using the tool is smart. No shame in outsourcing arithmetic.", Personality.FRIENDLY),
        Roast("This app runs a full floating-point unit's worth of logic just to save you from carrying the one.", Personality.NERD),
        Roast("You've got this. Somewhere in there is a division problem you can absolutely handle.", Personality.MOTIVATIONAL),
        Roast("You didn't need the calculator. You needed an excuse to stop thinking.", Personality.SAVAGE)
    )

    private val camera = listOf(
        Roast("You've opened the camera app three times and still haven't taken a photo.", Personality.SARCASTIC),
        Roast("Another selfie angle exploration session, I see.", Personality.FRIENDLY),
        Roast("Your camera roll is ninety percent blurry ceilings and pockets.", Personality.SAVAGE),
        Roast("You opened the camera and immediately forgot why.", Personality.DEADPAN),
        Roast("Smile. No one's watching except your own reflection.", Personality.SARCASTIC),
        Roast("That's a twelve-megapixel sensor pointed squarely at indecision.", Personality.NERD),
        Roast("You've got this. Somewhere in this session is a photo actually worth keeping.", Personality.MOTIVATIONAL),
        Roast("Every angle so far has been rejected. The photo shoot continues.", Personality.DEADPAN),
        Roast("You're not taking a photo. You're auditioning for one.", Personality.SAVAGE)
    )

    private val spotify = listOf(
        Roast("Same three songs on repeat, bold choice.", Personality.SARCASTIC),
        Roast("You've been \"discovering weekly\" the same five artists for a year.", Personality.SAVAGE),
        Roast("Skipping to the chorus again? Commitment issues confirmed.", Personality.SARCASTIC),
        Roast("Your playlist names are more creative than your actual plans today.", Personality.FRIENDLY),
        Roast("That song's not going to finish itself if you keep skipping it.", Personality.DEADPAN),
        Roast("Your skip rate would make any recommendation algorithm question its life choices.", Personality.NERD),
        Roast("You've got great taste. Using approximately six percent of it today.", Personality.MOTIVATIONAL),
        Roast("Shuffle is doing more decision-making than you are right now.", Personality.DEADPAN),
        Roast("That's not a playlist, that's a comfort blanket with a play button.", Personality.SAVAGE)
    )

    private val unknownCommand = listOf(
        Roast("That command doesn't exist. Bold guess though.", Personality.SARCASTIC),
        Roast("Nice try. That's not a command, that's a typo.", Personality.SARCASTIC),
        Roast("The terminal has no idea what you just typed. Neither do I.", Personality.DEADPAN),
        Roast("Even autocorrect gave up on that one.", Personality.SAVAGE),
        Roast("That's not a command. Type \"help\" before you improvise again.", Personality.DEADPAN),
        Roast("Hey, creative guess. Wrong, but creative.", Personality.FRIENDLY),
        Roast("That input matched zero registered patterns. Zero matches, zero regrets, apparently.", Personality.NERD),
        Roast("You've got this. Try again, this time with an actual command.", Personality.MOTIVATIONAL),
        Roast("You typed that with real confidence. The terminal remains unconvinced.", Personality.SAVAGE)
    )

    private val batteryUnder15 = listOf(
        Roast("Your battery lasts fifteen percent longer than your attention span will.", Personality.SARCASTIC),
        Roast("Fifteen percent and still scrolling? Bold strategy.", Personality.SARCASTIC),
        Roast("Your phone is basically begging for a charger right now.", Personality.FRIENDLY),
        Roast("At this battery level, even the phone wants to nap.", Personality.DEADPAN),
        Roast("Low battery, high denial. Classic combination.", Personality.SAVAGE),
        Roast("You've got maybe twenty minutes left. Choose your next scroll wisely.", Personality.MOTIVATIONAL),
        Roast("The power management system is one percent away from a graceful shutdown, and so are you.", Personality.NERD),
        Roast("That red battery icon isn't a suggestion, it's a warning label.", Personality.DEADPAN),
        Roast("You're really going to ride this down to zero, aren't you.", Personality.SAVAGE)
    )

    private val batteryCharging = listOf(
        Roast("Plugged in and still glued to the screen. Efficient, at least.", Personality.SARCASTIC),
        Roast("Charging the battery won't charge your motivation.", Personality.SAVAGE),
        Roast("Your phone's recovering faster than your sleep schedule ever will.", Personality.DEADPAN),
        Roast("Tethered to the wall and still can't put it down.", Personality.SARCASTIC),
        Roast("Hey, at least something around here is recharging properly.", Personality.FRIENDLY),
        Roast("Current's flowing into the battery at a healthier rate than productivity is flowing into your day.", Personality.NERD),
        Roast("You've got this. While it charges, maybe you could too.", Personality.MOTIVATIONAL),
        Roast("The cable's doing all the effort here. You're just holding a phone.", Personality.DEADPAN)
    )

    private val batteryFull = listOf(
        Roast("Your battery has more energy than you do.", Personality.SARCASTIC),
        Roast("Battery's at one hundred percent. Your excuses, unfortunately, aren't running low.", Personality.SAVAGE),
        Roast("Full charge, same empty scrolling habits.", Personality.DEADPAN),
        Roast("One hundred percent battery, zero percent urgency to do anything productive.", Personality.SARCASTIC),
        Roast("Nice, fully charged. Use it for something other than this.", Personality.FRIENDLY),
        Roast("One hundred percent capacity, and roughly none of it allocated to anything useful.", Personality.NERD),
        Roast("You've got a full tank now. Might as well go somewhere with it.", Personality.MOTIVATIONAL),
        Roast("Fully charged and fully committed to doing nothing with it.", Personality.SAVAGE)
    )

    private val morning = listOf(
        Roast("Up early and already on the phone? Bold start.", Personality.SARCASTIC),
        Roast("The sun's barely up and neither is your motivation.", Personality.DEADPAN),
        Roast("Good morning. Your to-do list says otherwise.", Personality.SARCASTIC),
        Roast("Coffee hasn't kicked in but the scrolling already has.", Personality.DEADPAN),
        Roast("Hey, you're up. That's already a win, technically.", Personality.FRIENDLY),
        Roast("Your cortisol levels peaked an hour ago and you spent it on a screen.", Personality.NERD),
        Roast("You've got this. Today's the day the to-do list gets touched. Probably.", Personality.MOTIVATIONAL),
        Roast("The day hasn't started, and somehow you're already behind.", Personality.SAVAGE)
    )

    private val afternoon = listOf(
        Roast("Afternoon slump, meet afternoon scroll.", Personality.DEADPAN),
        Roast("Lunch break's over. This isn't lunch anymore.", Personality.SARCASTIC),
        Roast("The day's half gone and so is your focus.", Personality.SAVAGE),
        Roast("Productive afternoon? Ask your screen time report.", Personality.SARCASTIC),
        Roast("Hey, the day's not over. There's still time to turn it around.", Personality.FRIENDLY),
        Roast("Post-lunch blood sugar dip confirmed, alongside your entire attention span.", Personality.NERD),
        Roast("You've got this. Second wind's around here somewhere.", Personality.MOTIVATIONAL),
        Roast("Half the day's gone and the phone won this round too.", Personality.SAVAGE)
    )

    private val night = listOf(
        Roast("It's late. The phone doesn't need you awake for this.", Personality.DEADPAN),
        Roast("One more scroll before bed, right? That was an hour ago.", Personality.SARCASTIC),
        Roast("Your sleep schedule sends its regards, or lack thereof.", Personality.SARCASTIC),
        Roast("Even your phone's night mode is judging your bedtime.", Personality.SAVAGE),
        Roast("Hey, it's okay. Just put it down when you're ready.", Personality.FRIENDLY),
        Roast("Blue light filter's on. Your circadian rhythm remains unimpressed.", Personality.NERD),
        Roast("You've got this. Tomorrow's the day you actually go to bed on time.", Personality.MOTIVATIONAL),
        Roast("The bed is right there. So is the phone, unfortunately winning.", Personality.SAVAGE)
    )

    private val general = listOf(
        Roast("Your battery lasts longer than your attention span.", Personality.SARCASTIC),
        Roast("You call that multitasking? Even your RAM is judging you.", Personality.NERD),
        Roast("404: Productivity not found.", Personality.DEADPAN),
        Roast("You've refreshed that app five times. It's not going to text back faster.", Personality.SAVAGE),
        Roast("Your Wi-Fi signal has more commitment issues than you do.", Personality.SARCASTIC),
        Roast("Even your screen time report is disappointed in you.", Personality.SAVAGE),
        Roast("You've been staring at this terminal longer than you've read the manual.", Personality.DEADPAN),
        Roast("Your phone storage is fuller than your schedule, and that's saying something.", Personality.SARCASTIC),
        Roast("You type like autocorrect is your only friend.", Personality.SAVAGE),
        Roast("Even airplane mode gets more silence than your notifications deserve.", Personality.SARCASTIC),
        Roast("Your app switching speed says a lot about your decision-making skills.", Personality.DEADPAN),
        Roast("You've unlocked your phone more times today than you've finished a task.", Personality.DEADPAN),
        Roast("Somewhere, a loading spinner is judging your patience.", Personality.SARCASTIC),
        Roast("Your battery percentage drops faster than your motivation on Mondays.", Personality.SAVAGE),
        Roast("You've typed \"lol\" more than you've actually laughed today.", Personality.DEADPAN),
        Roast("Even your flashlight app has seen more action than your to-do list.", Personality.SARCASTIC),
        Roast("You checked notifications again? Bold of you to expect good news.", Personality.SAVAGE),
        Roast("Your clipboard history is basically a diary of regret.", Personality.SARCASTIC),
        Roast("You've dismissed more pop-ups than actual problems in your life.", Personality.SAVAGE),
        Roast("Your app icons are more organized than your life choices.", Personality.DEADPAN),
        Roast("You've googled the same question three times this week.", Personality.SARCASTIC),
        Roast("Your phone's storage warning has more urgency than your deadlines.", Personality.DEADPAN),
        Roast("You scroll faster than you read. Impressive, honestly.", Personality.SARCASTIC),
        Roast("Your alarm snooze count could power a small city.", Personality.NERD),
        Roast("You've opened the fridge app more than the actual fridge.", Personality.SARCASTIC),
        Roast("Your typing speed peaks only when arguing online.", Personality.SAVAGE),
        Roast("Even your screen brightness dims out of secondhand embarrassment.", Personality.SARCASTIC),
        Roast("You've bookmarked more articles than you'll ever read.", Personality.DEADPAN),
        Roast("Your battery optimization settings work harder than your morning routine.", Personality.NERD),
        Roast("404: Motivation not found.", Personality.DEADPAN),
        Roast("Your notifications have given up trying to get your attention.", Personality.SAVAGE),
        Roast("You've restarted your phone more times than you've restarted your goals.", Personality.SAVAGE),
        Roast("Even your screensaver has more direction than your evening plans.", Personality.SARCASTIC),
        Roast("You've typed and deleted that message so many times, it needs therapy.", Personality.SARCASTIC),
        Roast("Your app drawer has more organization than your actual drawers.", Personality.DEADPAN),
        Roast("Hey, for what it's worth, showing up to the terminal at all counts as effort.", Personality.FRIENDLY),
        Roast("You're doing fine. Statistically, someone's doing worse right now.", Personality.FRIENDLY),
        Roast("No judgment here. Well, mild judgment. Constructive judgment.", Personality.FRIENDLY),
        Roast("It's okay to not be productive right now. It's just funny that this is the alternative.", Personality.FRIENDLY),
        Roast("Hey, at least you're consistent. Consistently on your phone, but still.", Personality.FRIENDLY),
        Roast("You've got this. Whatever \"this\" turns out to be today.", Personality.MOTIVATIONAL),
        Roast("Believe in yourself. Someone should, and it might as well start here.", Personality.MOTIVATIONAL),
        Roast("Today's the day you turn it all around. Or, you know, tomorrow works too.", Personality.MOTIVATIONAL),
        Roast("Greatness is just outside your comfort zone, which is currently this terminal.", Personality.MOTIVATIONAL),
        Roast("You are capable of so much. This isn't that, but you are.", Personality.MOTIVATIONAL),
        Roast("Your dopamine receptors have fully adapted to variable-ratio reinforcement, which is the same mechanism slot machines use.", Personality.NERD),
        Roast("Every notification you get triggers the exact same neurochemical response as a tiny reward. Working as intended, unfortunately.", Personality.NERD),
        Roast("Your phone's uptime is longer than most of your goals' lifespans.", Personality.NERD),
        Roast("This terminal has processed more of your commands than your brain has processed your priorities today.", Personality.DEADPAN),
        Roast("Fact: you have opened this phone more times today than you've made eye contact with another human.", Personality.DEADPAN),
        Roast("The pattern is clear. The pattern is not good.", Personality.DEADPAN),
        Roast("You are, statistically, mid-scroll right now. Statistically, always.", Personality.DEADPAN),
        Roast("That's a new record for staring at a screen without absorbing anything.", Personality.SARCASTIC),
        Roast("Bold of you to assume this counts as a break.", Personality.SARCASTIC),
        Roast("Ah yes, another deeply necessary phone check.", Personality.SARCASTIC),
        Roast("Truly inspiring focus. Wrong direction, but inspiring.", Personality.SARCASTIC),
        Roast("You've mastered the art of being busy while accomplishing nothing.", Personality.SAVAGE),
        Roast("At some point \"later\" became a permanent address for your responsibilities.", Personality.SAVAGE),
        Roast("You're not procrastinating. You're actively practicing avoidance, which takes real dedication.", Personality.SAVAGE),
        Roast("Your to-do list called. It's given up too.", Personality.SAVAGE)
    )
}
