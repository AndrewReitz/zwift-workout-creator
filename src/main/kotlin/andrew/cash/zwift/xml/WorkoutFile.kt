package andrew.cash.zwift.xml

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "workout_file")
@JsonPropertyOrder(value = ["author", "name", "description", "sportType", "workout"])
data class WorkoutFile(
        val author: String,
        val name: String,
        val description: String,

        val workout: Workout
) {
    val sportType = "bike"
}