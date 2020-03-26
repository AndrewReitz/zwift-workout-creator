package andrew.cash.zwift.xml

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun Duration.toXmlSeconds(): Int = inSeconds.toInt()

fun Int.toXmlPower(): Double = toDouble() / 100.0