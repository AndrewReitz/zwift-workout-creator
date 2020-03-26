package andrew.cash.zwift.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "WarmUp")
data class Warmup(
        @get:JacksonXmlProperty(isAttribute = true) val Duration: Int,
        @get:JacksonXmlProperty(isAttribute = true) val PowerLow: Double?,
        @get:JacksonXmlProperty(isAttribute = true) val PowerHigh: Double?
)