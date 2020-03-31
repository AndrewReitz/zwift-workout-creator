package andrew.cash.zwift.xml

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JacksonXmlRootElement(localName = "workout_file")
@JsonPropertyOrder(value = ["author", "name", "description", "sportType", "workout"])
data class WorkoutFile(
        val author: String,
        val name: String,
        val description: String,

        @get:JacksonXmlProperty(localName = "workout")
        @get:JsonSerialize(using = ExerciseXmlSerializer::class)
        val workouts: List<Exercise> = listOf()
) {
    val sportType = "bike"
}