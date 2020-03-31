package andrew.cash.zwift

import andrew.cash.zwift.input.CadenceLadder
import andrew.cash.zwift.input.CoolDown
import andrew.cash.zwift.input.Custom
import andrew.cash.zwift.input.FTPInterval
import andrew.cash.zwift.input.FatBurner
import andrew.cash.zwift.input.SingleLeg
import andrew.cash.zwift.input.TextEvent
import andrew.cash.zwift.input.VO2
import andrew.cash.zwift.input.WarmUp
import andrew.cash.zwift.input.Z5R
import andrew.cash.zwift.input.Z5R.Type.Cadence
import ch.tutteli.spek.extensions.memoizedTempFolder
import com.google.common.truth.Truth.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.nio.file.Path
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.minutes
import kotlin.time.seconds


@ExperimentalTime
object WorkoutSpec : Spek({
  Feature("Workout Input") {

    val tempDir by memoizedTempFolder()
    lateinit var output: Path

    beforeEachScenario {
      val input = tempDir.newFile("empty.zwo.kts").toAbsolutePath()
      output = tempDir.newFile("output.zwo")
      zwoCreate.main(arrayOf(input.toString(), output.toAbsolutePath().toString()))
    }

    Scenario("headers") {

      When("creating a workout") {
        Workout(
          name = "Lover",
          author = "Taylor Swift",
          restpower = 75,
          exercise = listOf()
        )
      }

      Then("output should have required headers") {
        assertThat(output.readText()).isEqualTo(
          """
            |<workout_file>
            |  <author>Taylor Swift</author>
            |  <name>Lover</name>
            |  <description></description>
            |  <sportType>bike</sportType>
            |  <workout/>
            |</workout_file>
            |""".trimMargin()
        )
      }
    }

    Scenario("WarmUp") {

      When("create workout with warmup") {
        Workout(
          name = "Lover",
          author = "Taylor Swift",
          restpower = 75,
          exercise = listOf(WarmUp(1.minutes))
        )
      }

      Then("output should have warmup in workout") {
        assertThat(output.readText()).isEqualTo(
          """
          |<workout_file>
          |  <author>Taylor Swift</author>
          |  <name>Lover</name>
          |  <description>WarmUp</description>
          |  <sportType>bike</sportType>
          |  <workout>
          |    <Warmup Duration="60" PowerLow="0.25" PowerHigh="0.75"/>
          |  </workout>
          |</workout_file>
          |""".trimMargin()
        )
      }
    }

    Scenario("CoolDown") {

      When("creating workout with cooldown") {
        Workout(
          name = "Lover",
          author = "Taylor Swift",
          restpower = 75,
          exercise = listOf(CoolDown(1.hours))
        )
      }

      Then("output should have cooldown in workout") {
        assertThat(output.readText()).isEqualTo(
          """
          |<workout_file>
          |  <author>Taylor Swift</author>
          |  <name>Lover</name>
          |  <description>CoolDown</description>
          |  <sportType>bike</sportType>
          |  <workout>
          |    <Cooldown Duration="3600" PowerLow="0.75" PowerHigh="0.25"/>
          |  </workout>
          |</workout_file>
          |""".trimMargin()
        )
      }
    }

    Scenario("SingleLeg") {

      When("create workout with single leg drill") {
        Workout(
          name = "22",
          author = "Taylor Swift",
          restpower = 75,
          exercise = listOf(
            SingleLeg(
              sets = 2,
              power = 76
            )
          )
        )
      }

      Then("output should have single leg drills in workout") {
        assertThat(output.readText()).isEqualTo(
          """
          |<workout_file>
          |  <author>Taylor Swift</author>
          |  <name>22</name>
          |  <description>SingleLeg</description>
          |  <sportType>bike</sportType>
          |  <workout>
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
          |  </workout>
          |</workout_file>
          |""".trimMargin()
        )
      }
    }

    Scenario("CadenceLadder") {

      When("create workout with cadence ladder") {
        Workout(
          name = "...Are you ready for it?",
          author = "Taylor Swift",
          restpower = 75,
          exercise = listOf(CadenceLadder())
        )
      }

      Then("output should have cadence ladder in workout") {
        assertThat(output.readText()).isEqualTo(
          """
          |<workout_file>
          |  <author>Taylor Swift</author>
          |  <name>...Are you ready for it?</name>
          |  <description>CadenceLadder</description>
          |  <sportType>bike</sportType>
          |  <workout>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="85 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="90 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="95 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="100 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="105 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="110 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="115 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="120 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="115 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="110 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="105 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="100 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="95 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="90 RPM"/>
          |    </SteadyState>
          |    <SteadyState Duration="60" Power="0.9">
          |      <textevent timeoffset="0" message="85 RPM"/>
          |    </SteadyState>
          |  </workout>
          |</workout_file>
          |""".trimMargin()
        )
      }
    }

    Scenario("Z5R") {

      When("creating a workout with z5r exercise") {
        Workout(
          name = "Lover",
          author = "Taylor Swift",
          restpower = 75,
          exercise = listOf(
            Z5R(sets = 2, duration = 1.minutes, power = 105, type = Cadence, thresholdDuration = 30.seconds)
          )
        )
      }

      Then("output should have z5r data") {
        assertThat(output.readText()).isEqualTo(
          """
            |<workout_file>
            |  <author>Taylor Swift</author>
            |  <name>Lover</name>
            |  <description>Z5R</description>
            |  <sportType>bike</sportType>
            |  <workout>
            |    <SteadyState Duration="60" Power="1.05">
            |      <textevent timeoffset="0" message="Start at threshold"/>
            |      <textevent timeoffset="30" message="Spin up to VO2 Max!"/>
            |    </SteadyState>
            |    <SteadyState Duration="60" Power="0.75"/>
            |    <SteadyState Duration="60" Power="1.05">
            |      <textevent timeoffset="0" message="Start at threshold"/>
            |      <textevent timeoffset="30" message="Spin up to VO2 Max!"/>
            |    </SteadyState>
            |    <SteadyState Duration="60" Power="0.75"/>
            |  </workout>
            |</workout_file>
            |""".trimMargin()
        )
      }
    }

    Scenario("VO2") {

      When("create workout with VO2 in it") {
        Workout(
          name = "22",
          author = "Taylor Swift",
          restpower = 75,
          exercise = listOf(
            VO2(
              sets = 2,
              duration = 3.minutes,
              power = 120
            )
          )
        )
      }

      Then("output should have VO2 in workout") {
        assertThat(output.readText()).isEqualTo(
          """
          |<workout_file>
          |  <author>Taylor Swift</author>
          |  <name>22</name>
          |  <description>VO2</description>
          |  <sportType>bike</sportType>
          |  <workout>
          |    <SteadyState Duration="180" Power="1.2"/>
          |    <SteadyState Duration="90" Power="0.75"/>
          |    <SteadyState Duration="180" Power="1.2"/>
          |    <SteadyState Duration="90" Power="0.75"/>
          |  </workout>
          |</workout_file>
          |""".trimMargin()
        )
      }
    }

    Scenario("FatBurner") {

      When("create workout with FatBurner in it") {
        Workout(
          name = "You Need to Calm Down",
          author = "Taylor Swift",
          restpower = 75,
          exercise = listOf(
            FatBurner(
              sets = 2,
              duration = 2.minutes,
              power = 120
            )
          )
        )
      }

      Then("output should have fat burner in workout") {
        assertThat(output.readText()).isEqualTo(
          """
          |<workout_file>
          |  <author>Taylor Swift</author>
          |  <name>You Need to Calm Down</name>
          |  <description>FatBurner</description>
          |  <sportType>bike</sportType>
          |  <workout>
          |    <SteadyState Duration="120" Power="1.2">
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
          |    <SteadyState Duration="60" Power="0.75"/>
          |    <SteadyState Duration="120" Power="1.2">
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
          |    <SteadyState Duration="60" Power="0.75"/>
          |  </workout>
          |</workout_file>
          |""".trimMargin()
        )
      }
    }

    Scenario("FTPInterval") {

      When("create workout with FTPInterval in it") {
        Workout(
          name = "Welcome to New York",
          author = "Taylor Swift",
          restpower = 83,
          exercise = listOf(
            FTPInterval(
              sets = 2,
              duration = 20.minutes,
              power = 96
            )
          )
        )
      }

      Then("output should have FTP Intervals in workout") {
        assertThat(output.readText()).isEqualTo(
          """
          |<workout_file>
          |  <author>Taylor Swift</author>
          |  <name>Welcome to New York</name>
          |  <description>FTPInterval</description>
          |  <sportType>bike</sportType>
          |  <workout>
          |    <SteadyState Duration="1200" Power="0.96"/>
          |    <SteadyState Duration="400" Power="0.83"/>
          |    <SteadyState Duration="1200" Power="0.96"/>
          |    <SteadyState Duration="400" Power="0.83"/>
          |  </workout>
          |</workout_file>
          |""".trimMargin()
        )
      }
    }

    Scenario("Custom") {
      When("create workout with Custom in it") {
        Workout(
          name = "Welcome to New York",
          author = "Taylor Swift",
          restpower = 83,
          exercise = listOf(
            Custom(
              sets = 2,
              duration = 20.minutes,
              power = 96,
              cadence = 90,
              cadenceLow = 20,
              cadenceHigh = 100,
              powerLow = 10,
              powerHigh = 100,
              zone = 5,
              slop = 100,
              textEvents = listOf(TextEvent("GO GO GO")),
              name = "ALL THE THINGS!"
            )
          )
        )
      }

      Then("output should have FTP Intervals in workout") {
        assertThat(output.readText()).isEqualTo(
          """
          |<workout_file>
          |  <author>Taylor Swift</author>
          |  <name>Welcome to New York</name>
          |  <description>ALL THE THINGS!</description>
          |  <sportType>bike</sportType>
          |  <workout>
          |    <SteadyState Duration="1200" Power="0.96" Cadence="90" CadenceHigh="100" CadenceLow="20" PowerHigh="1.0" PowerLow="0.1" Zone="5" Slop="100">
          |      <textevent timeoffset="0" message="GO GO GO"/>
          |    </SteadyState>
          |    <SteadyState Duration="1200" Power="0.96" Cadence="90" CadenceHigh="100" CadenceLow="20" PowerHigh="1.0" PowerLow="0.1" Zone="5" Slop="100">
          |      <textevent timeoffset="0" message="GO GO GO"/>
          |    </SteadyState>
          |  </workout>
          |</workout_file>
          |""".trimMargin()
        )
      }
    }
  }
})
