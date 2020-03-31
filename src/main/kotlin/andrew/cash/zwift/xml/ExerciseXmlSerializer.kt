package andrew.cash.zwift.xml

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator

/**
 * To work around jackson issue
 * https://github.com/FasterXML/jackson-dataformat-xml/issues/230
 */
@Suppress("unused")
class ExerciseXmlSerializer(t: Class<List<Exercise>>?) : StdSerializer<List<Exercise>>(t) {

  /** Needed by Jackson to create. */
  constructor(): this(null)

  override fun serialize(value: List<Exercise>, gen: JsonGenerator, provider: SerializerProvider) {
    val xmlGen = gen as ToXmlGenerator
    xmlGen.writeStartObject()
    value.forEach {exercise ->
      val annotation = exercise::class.annotations.find { it is JacksonXmlRootElement } as? JacksonXmlRootElement
      xmlGen.writeFieldName(annotation?.localName ?: exercise::class.simpleName)

      xmlGen.writeObject(exercise)
    }
    gen.writeEndObject()
  }
}