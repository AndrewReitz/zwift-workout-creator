package andrew.cash.zwift.xml

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.google.common.truth.Truth.assertThat
import org.spekframework.spek2.Spek

object ExerciseXmlSerializerSpec : Spek({
  val xmlMapper by memoized {
    XmlMapper.builder()
      .defaultUseWrapper(false)
      .enable(SerializationFeature.INDENT_OUTPUT)
      .build()
  }

  test("should serialize work properly") {
    val test = TestClass(
      listOf(
        WarmUp(10, 75.0, 100.0),
        SteadyState(duration = 60, power = 1.0, cadence = 40),
        CoolDown(120, 0.50, 1.0)
      )
    )

    val result = xmlMapper.writeValueAsString(test)
    assertThat(result).isEqualTo("""
    |<TestClass>
    |  <workout>
    |    <Warmup Duration="10" PowerLow="75.0" PowerHigh="100.0"/>
    |    <SteadyState Duration="60" Power="1.0" Cadence="40"/>
    |    <Cooldown Duration="120" PowerLow="0.5" PowerHigh="1.0"/>
    |  </workout>
    |</TestClass>
    |""".trimMargin())
  }
}) {
  data class TestClass(
    @get:JacksonXmlProperty(localName = "workout")
    @get:JsonSerialize(using = ExerciseXmlSerializer::class)
    val exercises: List<Exercise> = listOf()
  )
}