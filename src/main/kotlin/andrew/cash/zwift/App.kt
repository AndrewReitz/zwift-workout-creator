package andrew.cash.zwift

import andrew.cash.zwift.input.CadenceLadder
import andrew.cash.zwift.input.CoolDown
import andrew.cash.zwift.input.Custom
import andrew.cash.zwift.input.Exercise
import andrew.cash.zwift.input.FTPInterval
import andrew.cash.zwift.input.FatBurner
import andrew.cash.zwift.input.SingleLeg
import andrew.cash.zwift.input.VO2
import andrew.cash.zwift.input.WarmUp
import andrew.cash.zwift.input.Workout
import andrew.cash.zwift.input.Z5R
import andrew.cash.zwift.xml.SteadyState
import andrew.cash.zwift.xml.TextEvent
import andrew.cash.zwift.xml.WorkoutFile
import andrew.cash.zwift.xml.defaultFatBurner
import andrew.cash.zwift.xml.textEvents
import andrew.cash.zwift.xml.toXml
import andrew.cash.zwift.xml.toXmlPower
import andrew.cash.zwift.xml.toXmlSeconds
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.time.ExperimentalTime
import andrew.cash.zwift.xml.Exercise as XmlExercise

/** Error writing to the specified output. Display message then terminate. */
class OutputException(override val message: String) : Exception()

class ZwoCreate : CliktCommand() {

  private val force by option("-f", "--force").flag()

  private val input: Path by argument()
    .path(
      canBeDir = false,
      mustExist = true,
      mustBeReadable = true
    )

  private val _output: Path? by argument(name = "OUTPUT").path().optional()
  val output: Path
    get() {
      val out = _output ?: return Paths.get("", "workout.zwo")

      val returnValue = when {
        out.toFile().isDirectory -> out.resolve("workout.zwo")
        !out.fileName.toString().contains(".zwo") -> out.parent.resolve("${out.fileName}.zwo")
        else -> out
      }

      if (!out.parent.toFile().exists()) {
        if (force) {
          if (!out.parent.toFile().mkdirs()) {
            throw OutputException("Could not create ${out.toAbsolutePath()}")
          }
        } else {
          throw OutputException("Output directory does not exist if \"$commandName\" should create it re-run with -f flag")
        }
      }

      return returnValue
    }

  override fun run() {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ZwoScript>() {
      jvm {
        compilerOptions("-Xopt-in=kotlin.time.ExperimentalTime")
        defaultImports(
          "andrew.cash.zwift.input.*",
          "andrew.cash.zwift.input.Z5R.Type.*",
          "andrew.cash.zwift.Workout",
          "kotlin.time.*"
        )
        dependenciesFromCurrentContext(wholeClasspath = true)
      }
      ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
      }
    }

    val result = BasicJvmScriptingHost().eval(input.toFile().toScriptSource(), compilationConfiguration, null)

    result.reports
      .filter { it.severity == ScriptDiagnostic.Severity.ERROR }
      .forEach { (message, _, sourcePath, location: SourceCode.Location?, _) ->
        val sourceFile = Paths.get(requireNotNull(sourcePath))
        echo("There was an error with script \"${sourceFile.fileName}\" at line ${location?.start?.line}: $message")
      }
  }
}

val zwoCreate = ZwoCreate()

@ExperimentalTime
fun main(args: Array<String>) = try {
  zwoCreate.main(args)
} catch (e: OutputException) {
  TermUi.echo(
    message = e.message,
    err = true
  )
}

// Main entry point from script
@Suppress("FunctionName")
@ExperimentalTime
fun Workout(name: String, author: String, restpower: Int, exercise: List<Exercise>) {
  val workout = Workout(
    author = author,
    name = name,
    restpower = restpower,
    exercise = exercise
  )

  val description = workout.exercise.joinToString(separator = ", ") { it.name }
  val defaultRest = SteadyState(duration = 0, power = workout.restpower.toXmlPower())

  val workouts = mutableListOf<XmlExercise>()

  workout.exercise.forEach { e ->
    val moreWorkouts: List<XmlExercise> = when (e) {
      is WarmUp -> listOf(e.toXml())
      is CoolDown -> listOf(e.toXml())
      is VO2 -> e.create(defaultRest)
      is FatBurner -> e.create(defaultRest)
      is FTPInterval -> e.create(defaultRest)
      is Z5R -> e.create(defaultRest)
      is CadenceLadder -> e.toXml()
      is SingleLeg -> e.create()
      is Custom -> e.toXml()
    }

    workouts.addAll(moreWorkouts)
  }

  val woFile = WorkoutFile(
    author = workout.author,
    name = workout.name,
    description = description,
    workouts = workouts
  )

  val xmlMapper = XmlMapper.builder()
    .defaultUseWrapper(false)
    .enable(SerializationFeature.INDENT_OUTPUT)
    .build()

  Files.newBufferedWriter(zwoCreate.output).use {
    xmlMapper.writeValue(it, woFile)
  }
}

@ExperimentalTime
private fun FTPInterval.create(
  defaultRest: SteadyState
): List<XmlExercise> {
  val workInterval = SteadyState(
    duration = duration.toXmlSeconds(),
    cadence = cadence,
    power = power.toXmlPower()
  )

  val restInterval = defaultRest.copy(duration = rest.toXmlSeconds())

  return List(sets) { listOf(workInterval, restInterval) }.flatten()
}

@ExperimentalTime
private fun FatBurner.create(
  defaultRest: SteadyState
): List<XmlExercise> {
  val workInterval = defaultFatBurner.copy(
    duration = duration.toXmlSeconds(),
    power = power.toXmlPower()
  )
  val restInterval = defaultRest.copy(duration = rest.toXmlSeconds())

  return List(sets) { listOf(workInterval, restInterval) }.flatten()
}

@ExperimentalTime
private fun VO2.create(defaultRest: SteadyState): List<XmlExercise> {
  val workInterval = SteadyState(
    duration = duration.inSeconds.toInt(),
    power = power.toXmlPower(),
    cadence = cadence
  )
  val restInterval = defaultRest.copy(duration = rest.toXmlSeconds())

  return List(sets) { listOf(workInterval, restInterval) }.flatten()
}

@ExperimentalTime
private fun Z5R.create(
  defaultRest: SteadyState
): List<XmlExercise> {
  val workInterval = SteadyState(
    power = power.toXmlPower(),
    textEvent = textEvents(),
    duration = duration.toXmlSeconds()
  )
  val restInterval = defaultRest.copy(duration = duration.toXmlSeconds())

  return List(sets) { listOf(workInterval, restInterval) }.flatten()
}

@ExperimentalTime
private fun SingleLeg.create(): List<XmlExercise> {
  val rightLeg = SteadyState(
    duration = duration.toXmlSeconds() / 2,
    cadence = cadence,
    power = power.toXmlPower(),
    textEvent = listOf(TextEvent(0, "Right Leg"))
  )
  val leftLeg = rightLeg.copy(textEvent = listOf(TextEvent(0, "Light Leg")))
  return  List(sets) { listOf(rightLeg, leftLeg) }.flatten()
}
