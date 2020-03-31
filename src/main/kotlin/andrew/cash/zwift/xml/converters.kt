package andrew.cash.zwift.xml

import andrew.cash.zwift.input.CadenceLadder
import andrew.cash.zwift.input.CoolDown
import andrew.cash.zwift.input.Custom
import andrew.cash.zwift.input.WarmUp
import andrew.cash.zwift.input.TextEvent
import andrew.cash.zwift.xml.WarmUp as XmlWarmUp
import andrew.cash.zwift.xml.CoolDown as XmlCoolDown
import andrew.cash.zwift.xml.TextEvent as XmlTextEvent
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@ExperimentalTime
fun Duration.toXmlSeconds(): Int = inSeconds.toInt()

fun Int.toXmlPower(): Double = toDouble() / 100.0

@ExperimentalTime
fun CadenceLadder.toXml() = listOf(
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "85 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "90 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "95 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "100 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "105 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "110 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "115 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "120 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "115 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "110 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "105 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "100 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "95 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "90 RPM"))),
  SteadyState(power = power.toXmlPower(), duration = 1.minutes.toXmlSeconds(), textEvent = listOf(XmlTextEvent(0, "85 RPM")))
)

@ExperimentalTime
fun WarmUp.toXml() = XmlWarmUp(
  duration = duration.toXmlSeconds(),
  powerLow = powerLow,
  powerHigh = powerHigh
)

@ExperimentalTime
fun CoolDown.toXml() = XmlCoolDown(
  duration = duration.toXmlSeconds(),
  powerLow = powerLow,
  powerHigh = powerHigh
)

@ExperimentalTime
fun Custom.toXml() = List(sets) {
  SteadyState(
    duration = duration.toXmlSeconds(),
    power = power?.toXmlPower(),
    cadence = cadence,
    textEvent = textEvents?.toXml(),
    cadenceHigh = cadenceHigh,
    cadenceLow = cadenceLow,
    powerHigh = powerHigh?.toXmlPower(),
    powerLow = powerLow?.toXmlPower(),
    zone = zone,
    slop = slop
  )
}

@ExperimentalTime
fun List<TextEvent>.toXml(): List<XmlTextEvent> = map { (message, time) ->
  XmlTextEvent(timeOffset = time.toXmlSeconds(), message = message)
}
