package andrew.cash.zwift.input

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

sealed class Exercise {
    val name: String get() = requireNotNull(this::class.simpleName)
}

/** High and low defaults come from Zwift's defaults */
@ExperimentalTime
data class WarmUp(val duration: Duration, val powerHigh: Double? = 0.75, val powerLow: Double? = 0.25) : Exercise()

/** High and low defaults come from Zwift's defaults */
@ExperimentalTime
data class CoolDown(val duration: Duration, val powerHigh: Double? = 0.25, val powerLow: Double? = 0.75) : Exercise()

interface RequiresRest {
    @ExperimentalTime
    val rest: Duration
}

/**
 * VO2: VO2 max intervals designed to build power necessary to cover accelera9ons and climb with a group.
 * Cadence is user selected, but should be similar to your preferred climbing cadence. Rest interval is half the
 * work interval at the day's
 */
@ExperimentalTime
data class VO2(val sets: Int, val duration: Duration, val power: Int) : Exercise(), RequiresRest {
    override val rest = duration.div(2)
}

/**
 * Fat Burner: Stay in 1 gear for each stage. Start with your easiest gear. A stage consists of: Start at 70 RPM,
 * add 5 RPM every 10 seconds (2 minutes of work) and 1 minute rest after work interval. Shift to the next hardest gear
 * for the next stage. Each stage counts as 1 rep (5 x fat burners means 5 stages/reps). Rest interval is performed
 * at the day's prescribed intensity.
 */
@ExperimentalTime
data class FatBurner(val sets: Int, val power: Int) : Exercise(), RequiresRest {
    override val rest = 1.minutes
}

/**
 * FTP: Threshold power and HR and sweet spot cadence (average cadence recorded for LTHR/FTP test.)
 * Rest interval is 1/3 the work interval.
 */
@ExperimentalTime
data class FTPInterval(val sets: Int, val duration: Duration, val power: Int, val cadence: Int? = null) : Exercise(), RequiresRest {
    override val rest = duration.div(3)
}