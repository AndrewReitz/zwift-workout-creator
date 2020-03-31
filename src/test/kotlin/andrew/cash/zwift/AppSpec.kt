package andrew.cash.zwift

import ch.tutteli.spek.extensions.memoizedTempFolder
import com.github.ajalt.clikt.core.MissingParameter
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.google.common.truth.Truth.assertThat
import org.spekframework.spek2.Spek
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.assertFailsWith
import kotlin.time.ExperimentalTime

@ExperimentalTime
object AppSpec: Spek({

  val outContent by memoized { ByteArrayOutputStream() }
  val errContent by memoized { ByteArrayOutputStream() }
  val tempDir by memoizedTempFolder()

  val originalOut = System.out
  val originalErr = System.err

  val outputText = { outContent.toString().trim() }
  val errorText = { errContent.toString().trim() }

  val assertNoErrorInOutput = {
    assertThat(outputText()).doesNotContain("There was an error with script")
  }

  beforeEachTest {
    System.setOut(PrintStream(outContent))
    System.setErr(PrintStream(errContent))
  }

  afterEachTest {
    System.setOut(originalOut)
    System.setErr(originalErr)
  }

  test("should fail when no input with useful error message") {
    val helpMessage = assertFailsWith<MissingParameter> {
      zwoCreate.parse(emptyArray())
    }.helpMessage()

    assertThat(helpMessage).isEqualTo("""
    Usage: zwocreate [OPTIONS] INPUT [OUTPUT]
    
    Error: Missing argument "INPUT".
    """.trimIndent())
  }

  test("should use default output when only input is provided") {

    val expectedOutput = File( "workout.zwo")

    val input = tempDir.newFile("input.zwo.kts")
    input.toFile().writeText("""
     Workout(
        author = "Rebecca Black",
        name = "Friday",
        restpower = 80,
        exercise = listOf(
          WarmUp(5.minutes),
          FatBurner(
            sets = 5,
            power =  100
          ),
          CadenceLadder(
            power = 100
          ),
          SingleLeg(
            sets = 8,
            power = 76,
            duration = 1.minutes
          ),
          CoolDown(5.minutes)
        )
      )
    """.trimIndent())

    main(arrayOf(input.toAbsolutePath().toString()))

    assertNoErrorInOutput()
    assertThat(expectedOutput.exists()).isTrue()

    expectedOutput.delete()
  }

  test("should output to output file specified") {
    val outputLocation = tempDir.tmpDir.resolve("friday-workout.zwo")

    val input = tempDir.newFile("input.zwo.kts")
    input.toFile().writeText("""
     Workout(
        author = "Rebecca Black",
        name = "Friday",
        restpower = 80,
        exercise = listOf(
          WarmUp(5.minutes),
          FatBurner(
            sets = 5,
            power =  100
          ),
          CadenceLadder(
            power = 100
          ),
          SingleLeg(
            sets = 8,
            power = 76,
            duration = 1.minutes
          ),
          CoolDown(5.minutes)
        )
      )
    """.trimIndent())

    main(arrayOf(input.toAbsolutePath().toString(), outputLocation.toAbsolutePath().toString()))

    assertNoErrorInOutput()
    assertThat(outputLocation.exists()).isTrue()

    assertThat(outputLocation.readText()).isEqualTo("""
    |<workout_file>
    |  <author>Rebecca Black</author>
    |  <name>Friday</name>
    |  <description>WarmUp, FatBurner, CadenceLadder, SingleLeg, CoolDown</description>
    |  <sportType>bike</sportType>
    |  <workout>
    |    <Warmup Duration="300" PowerLow="0.25" PowerHigh="0.75"/>
    |    <SteadyState Duration="120" Power="1.0">
    |      <textevent timeoffset="0" message="Cadence of 70"/>
    |      <textevent timeoffset="10" message="Cadence of 75"/>
    |      <textevent timeoffset="20" message="Cadence of 80"/>
    |      <textevent timeoffset="30" message="Cadence of 85"/>
    |      <textevent timeoffset="40" message="Cadence of 90"/>
    |      <textevent timeoffset="50" message="Cadence of 95"/>
    |      <textevent timeoffset="60" message="Cadence of 100"/>
    |      <textevent timeoffset="70" message="Cadence of 105"/>
    |      <textevent timeoffset="80" message="Cadence of 110"/>
    |      <textevent timeoffset="90" message="Cadence of 115"/>
    |      <textevent timeoffset="100" message="Cadence of 120"/>
    |      <textevent timeoffset="110" message="Cadence of 125"/>
    |      <textevent timeoffset="120" message="Cadence of 130"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="0.8"/>
    |    <SteadyState Duration="120" Power="1.0">
    |      <textevent timeoffset="0" message="Cadence of 70"/>
    |      <textevent timeoffset="10" message="Cadence of 75"/>
    |      <textevent timeoffset="20" message="Cadence of 80"/>
    |      <textevent timeoffset="30" message="Cadence of 85"/>
    |      <textevent timeoffset="40" message="Cadence of 90"/>
    |      <textevent timeoffset="50" message="Cadence of 95"/>
    |      <textevent timeoffset="60" message="Cadence of 100"/>
    |      <textevent timeoffset="70" message="Cadence of 105"/>
    |      <textevent timeoffset="80" message="Cadence of 110"/>
    |      <textevent timeoffset="90" message="Cadence of 115"/>
    |      <textevent timeoffset="100" message="Cadence of 120"/>
    |      <textevent timeoffset="110" message="Cadence of 125"/>
    |      <textevent timeoffset="120" message="Cadence of 130"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="0.8"/>
    |    <SteadyState Duration="120" Power="1.0">
    |      <textevent timeoffset="0" message="Cadence of 70"/>
    |      <textevent timeoffset="10" message="Cadence of 75"/>
    |      <textevent timeoffset="20" message="Cadence of 80"/>
    |      <textevent timeoffset="30" message="Cadence of 85"/>
    |      <textevent timeoffset="40" message="Cadence of 90"/>
    |      <textevent timeoffset="50" message="Cadence of 95"/>
    |      <textevent timeoffset="60" message="Cadence of 100"/>
    |      <textevent timeoffset="70" message="Cadence of 105"/>
    |      <textevent timeoffset="80" message="Cadence of 110"/>
    |      <textevent timeoffset="90" message="Cadence of 115"/>
    |      <textevent timeoffset="100" message="Cadence of 120"/>
    |      <textevent timeoffset="110" message="Cadence of 125"/>
    |      <textevent timeoffset="120" message="Cadence of 130"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="0.8"/>
    |    <SteadyState Duration="120" Power="1.0">
    |      <textevent timeoffset="0" message="Cadence of 70"/>
    |      <textevent timeoffset="10" message="Cadence of 75"/>
    |      <textevent timeoffset="20" message="Cadence of 80"/>
    |      <textevent timeoffset="30" message="Cadence of 85"/>
    |      <textevent timeoffset="40" message="Cadence of 90"/>
    |      <textevent timeoffset="50" message="Cadence of 95"/>
    |      <textevent timeoffset="60" message="Cadence of 100"/>
    |      <textevent timeoffset="70" message="Cadence of 105"/>
    |      <textevent timeoffset="80" message="Cadence of 110"/>
    |      <textevent timeoffset="90" message="Cadence of 115"/>
    |      <textevent timeoffset="100" message="Cadence of 120"/>
    |      <textevent timeoffset="110" message="Cadence of 125"/>
    |      <textevent timeoffset="120" message="Cadence of 130"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="0.8"/>
    |    <SteadyState Duration="120" Power="1.0">
    |      <textevent timeoffset="0" message="Cadence of 70"/>
    |      <textevent timeoffset="10" message="Cadence of 75"/>
    |      <textevent timeoffset="20" message="Cadence of 80"/>
    |      <textevent timeoffset="30" message="Cadence of 85"/>
    |      <textevent timeoffset="40" message="Cadence of 90"/>
    |      <textevent timeoffset="50" message="Cadence of 95"/>
    |      <textevent timeoffset="60" message="Cadence of 100"/>
    |      <textevent timeoffset="70" message="Cadence of 105"/>
    |      <textevent timeoffset="80" message="Cadence of 110"/>
    |      <textevent timeoffset="90" message="Cadence of 115"/>
    |      <textevent timeoffset="100" message="Cadence of 120"/>
    |      <textevent timeoffset="110" message="Cadence of 125"/>
    |      <textevent timeoffset="120" message="Cadence of 130"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="0.8"/>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="85 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="90 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="95 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="100 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="105 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="110 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="115 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="120 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="115 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="110 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="105 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="100 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="95 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="90 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="60" Power="1.0">
    |      <textevent timeoffset="0" message="85 RPM"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Right Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Light Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Right Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Light Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Right Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Light Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Right Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Light Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Right Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Light Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Right Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Light Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Right Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Light Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Right Leg"/>
    |    </SteadyState>
    |    <SteadyState Duration="30" Power="0.76" Cadence="90">
    |      <textevent timeoffset="0" message="Light Leg"/>
    |    </SteadyState>
    |    <Cooldown Duration="300" PowerLow="0.75" PowerHigh="0.25"/>
    |  </workout>
    |</workout_file>
    |""".trimMargin())
  }

  test("should provide helpful error message if kts file is bad") {
    val outputLocation = tempDir.tmpDir.resolve("friday-workout.zwo")

    val input = tempDir.newFile("input.zwo.kts")
    input.toFile().writeText("""
     Workout(
        author = "Rebecca Black",
        name = "Friday",
        restpower = 80,
        exercise = listOf(
          SingleLeg(
            // sets = 8, Missing sets error
            power = 76,
            duration = 1.minutes
          )
        )
      )
    """.trimIndent())

    main(arrayOf(input.toAbsolutePath().toString(), outputLocation.toAbsolutePath().toString()))

    assertThat(outputText()).isEqualTo(
      "There was an error with script \"input.zwo.kts\" at line 10: No value passed for parameter 'sets'"
    )
  }

  test("should create a new folder if parent directories do not exist for output") {
    val outputLocation = tempDir.tmpDir.resolve("test/friday-workout.zwo")

    val input = tempDir.newFile("input.zwo.kts")
    input.toFile().writeText("""
     Workout(
        author = "Rebecca Black",
        name = "Friday",
        restpower = 80,
        exercise = listOf()
     )
    """.trimIndent())

    main(arrayOf(input.toAbsolutePath().toString(), outputLocation.toAbsolutePath().toString(), "-f"))

    assertThat(outputLocation.exists()).isTrue()
    assertThat(outputLocation.readText()).isEqualTo("""
        |<workout_file>
        |  <author>Rebecca Black</author>
        |  <name>Friday</name>
        |  <description></description>
        |  <sportType>bike</sportType>
        |  <workout/>
        |</workout_file>
        |""".trimMargin())
  }
})
