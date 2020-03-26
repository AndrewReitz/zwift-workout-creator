package andrew.cash.zwift.xml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class Workout(
        @get:JacksonXmlProperty(localName = "Warmup")
        val warmup: Warmup? = null,

        @get:JacksonXmlProperty(localName = "SteadyState")
        @get:JacksonXmlElementWrapper(useWrapping = false)
        val steadyState: List<SteadyState> = listOf(),

        @get:JacksonXmlProperty(localName = "Cooldown")
        val cooldown: Cooldown? = null
)