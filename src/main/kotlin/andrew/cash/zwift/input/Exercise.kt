package andrew.cash.zwift.input

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import kotlin.time.seconds

sealed class Exercise {
  open val name: String get() = requireNotNull(this::class.simpleName)
}

/** High and low defaults come from Zwift's defaults */
@ExperimentalTime
data class WarmUp(val duration: Duration, val powerHigh: Double? = 0.75, val powerLow: Double? = 0.25) : Exercise()

/** High and low defaults come from Zwift's defaults */
@ExperimentalTime
data class CoolDown(val duration: Duration, val powerHigh: Double? = 0.25, val powerLow: Double? = 0.75) : Exercise()

@ExperimentalTime
interface RequiresRest {
  val rest: Duration
}

/**
 * Single Leg: Single leg drills. Divide the work interval listed in half and do half each with lee and right legs.
 * Example: 1 minute work interval = 30 seconds with lee leg and 30 seconds with right leg. No rest interval.
 */
@ExperimentalTime
data class SingleLeg(
  val sets: Int,
  val power: Int,
  val cadence: Int = 90,
  val duration: Duration = 1.minutes
): Exercise()

/**
 * Cadence Ladder: Protocol is as follows: Start at about 75% FTP/75% LTHR. Start at 85 RPM cadence. Each minute
 * increase your cadence 5 RPM until you hit 120 RPM for 1 minute. Reverse and decrease cadence 5 rpm each minute
 * until you return to 85 RPM. Total 9me is 15 minutes without any rest or modification.
 */
@ExperimentalTime
data class CadenceLadder(val sets: Int = 1, val power: Int = 90): Exercise()

/**
 * Z5R: Zone 5 Repeats. Designed to force acceleration into VO2 max (zone 5) and recovery at threshold; mimics
 * race/peloton surges. See descriptions above for intensities. Time in zones is wriVen as (9me at threshold/9me at
 * Zone 5 VO2 max) and each interval is 1 threshold/VO2 cycle. Intervals may be designated as (cadence) or (shift) or
 * (both). If an interval is designated as (cadence), increase your cadence to get into zone 5. If an interval is
 * designated (shift) shift up to a harder gear to get into zone 5. If no designation is given, use whatever method
 * you prefer to increase power into zone 5, including standing if
 */
@ExperimentalTime
data class Z5R(
  val sets: Int,
  val power: Int,
  val type: Type,
  val duration: Duration = 1.minutes,
  val thresholdDuration: Duration = 30.seconds
) : Exercise() {
  sealed class Type {
    object Cadence : Z5R.Type()
    object Shift : Z5R.Type()
    object Both : Z5R.Type()
  }
}

/**
 * VO2: VO2 max intervals designed to build power necessary to cover accelera9ons and climb with a group.
 * Cadence is user selected, but should be similar to your preferred climbing cadence. Rest interval is half the
 * work interval at the day's
 */
@ExperimentalTime
data class VO2(
  val sets: Int,
  val duration: Duration,
  val power: Int,
  val cadence: Int? = null
) : Exercise(), RequiresRest {
  override val rest = duration.div(2)
}

/**
 * Fat Burner: Stay in 1 gear for each stage. Start with your easiest gear. A stage consists of: Start at 70 RPM,
 * add 5 RPM every 10 seconds (2 minutes of work) and 1 minute rest after work interval. Shift to the next hardest gear
 * for the next stage. Each stage counts as 1 rep (5 x fat burners means 5 stages/reps). Rest interval is performed
 * at the day's prescribed intensity.
 *
 * @param duration is only added for ease of filling out the dsl and is expected to always be 2 minutes
 */
@ExperimentalTime
data class FatBurner(val sets: Int, val power: Int, val duration: Duration = 2.minutes) : Exercise(), RequiresRest {
  override val rest = 1.minutes
}

/**
 * FTP: Threshold power and HR and sweet spot cadence (average cadence recorded for LTHR/FTP test.)
 * Rest interval is 1/3 the work interval.
 */
@ExperimentalTime
data class FTPInterval(val sets: Int, val duration: Duration, val power: Int, val cadence: Int? = null) : Exercise(),
  RequiresRest {
  override val rest = duration.div(3)
}

/**
 * Custom class for writing custom sets in Zwift. Contains values seen in steady state.
 * https://github.com/h4l/zwift-workout-file-reference/blob/master/zwift_workout_file_tag_reference.md#element-SteadyState
 * Note: There is not rest interval, one can be made with custom, and can
 * easily be repeated using the `repeat` function.
 */
@ExperimentalTime
data class Custom(
  val duration: Duration,
  val power: Int? = null,
  val sets: Int = 1,
  val cadence: Int? = null,
  val cadenceHigh: Int? = null,
  val cadenceLow: Int? = null,
  val powerHigh: Int? = null,
  val powerLow: Int? = null,
  val zone: Int? = null,
  val slop: Int? = null,
  val textEvents: List<TextEvent>? = null,
  override val name: String = Custom::class.java.name
): Exercise()

/**
 * Displays text at a certain specified time in the exercise.
 */
@ExperimentalTime
data class TextEvent(val text: String, val showAtTime: Duration = Duration.ZERO)
