package andrew.cash.zwift.xml

import andrew.cash.zwift.input.Z5R
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

sealed class Exercise

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JacksonXmlRootElement(localName = "Cooldown")
data class CoolDown(
  @get:JacksonXmlProperty(isAttribute = true, localName = "Duration") val duration: Int,
  @get:JacksonXmlProperty(isAttribute = true, localName = "PowerLow") val powerLow: Double?,
  @get:JacksonXmlProperty(isAttribute = true, localName = "PowerHigh") val powerHigh: Double?
): Exercise()

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JacksonXmlRootElement(localName = "Warmup")
data class WarmUp(
  @get:JacksonXmlProperty(isAttribute = true, localName = "Duration") val duration: Int,
  @get:JacksonXmlProperty(isAttribute = true, localName = "PowerLow") val powerLow: Double?,
  @get:JacksonXmlProperty(isAttribute = true, localName = "PowerHigh") val powerHigh: Double?
): Exercise()

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class SteadyState(
  @get:JacksonXmlProperty(isAttribute = true, localName = "Duration") val duration: Int,
  @get:JacksonXmlProperty(isAttribute = true, localName = "Power") val power: Double? = null,
  @get:JacksonXmlProperty(isAttribute = true, localName = "Cadence") val cadence: Int? = null,

  @get:JacksonXmlProperty(localName = "textevent")
  @get:JacksonXmlElementWrapper(useWrapping = false)
  val textEvent: List<TextEvent>? = null,

  // values after here are all from
  //https://github.com/h4l/zwift-workout-file-reference/blob/master/zwift_workout_file_tag_reference.md#element-SteadyState
  // and my nt actually do anything
  @get:JacksonXmlProperty(isAttribute = true, localName = "CadenceHigh") val cadenceHigh: Int? = null,
  @get:JacksonXmlProperty(isAttribute = true, localName = "CadenceLow") val cadenceLow: Int? = null,
  @get:JacksonXmlProperty(isAttribute = true, localName = "PowerHigh") val powerHigh: Double? = null,
  @get:JacksonXmlProperty(isAttribute = true, localName = "PowerLow") val powerLow: Double? = null,
  @get:JacksonXmlProperty(isAttribute = true, localName = "Zone") val zone: Int? = null,
  @get:JacksonXmlProperty(isAttribute = true, localName = "Slop") val slop: Int? = null
): Exercise()

@JacksonXmlRootElement(localName = "textevent")
data class TextEvent(
  @get:JacksonXmlProperty(isAttribute = true, localName = "timeoffset") val timeOffset: Int,
  @get:JacksonXmlProperty(isAttribute = true, localName = "message") val message: String
)

val defaultFatBurnerTextEvents = listOf(
  TextEvent(timeOffset = 0, message = "Cadence of 70"),
  TextEvent(timeOffset = 10, message = "Cadence of 75"),
  TextEvent(timeOffset = 20, message = "Cadence of 80"),
  TextEvent(timeOffset = 30, message = "Cadence of 85"),
  TextEvent(timeOffset = 40, message = "Cadence of 90"),
  TextEvent(timeOffset = 50, message = "Cadence of 95"),
  TextEvent(timeOffset = 60, message = "Cadence of 100"),
  TextEvent(timeOffset = 70, message = "Cadence of 105"),
  TextEvent(timeOffset = 80, message = "Cadence of 110"),
  TextEvent(timeOffset = 90, message = "Cadence of 115"),
  TextEvent(timeOffset = 100, message = "Cadence of 120"),
  TextEvent(timeOffset = 110, message = "Cadence of 125"),
  TextEvent(timeOffset = 120, message = "Cadence of 130")
)

@ExperimentalTime
val defaultFatBurner = SteadyState(
  duration = 2.minutes.toXmlSeconds(),
  textEvent = defaultFatBurnerTextEvents,
  power = 0.0
)

@ExperimentalTime
fun Z5R.textEvents() = listOf(
  TextEvent(0, "Start at threshold"),
  when (type) {
    Z5R.Type.Cadence -> TextEvent(thresholdDuration.toXmlSeconds(), "Spin up to VO2 Max!")
    Z5R.Type.Shift -> TextEvent(thresholdDuration.toXmlSeconds(), "Shift up to VO2 Max!")
    Z5R.Type.Both -> TextEvent(thresholdDuration.toXmlSeconds(), "Shift or Spin up to VO2 Max!")
  }
)
