package andrew.cash.zwift

import andrew.cash.zwift.input.*
import andrew.cash.zwift.xml.*
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.time.ExperimentalTime
import andrew.cash.zwift.xml.Workout as XmlWorkout
import andrew.cash.zwift.xml.Cooldown as XmlCooldown

@ExperimentalTime
fun main(args: Array<String>) {
    val input = if (args.isNotEmpty()) Paths.get(args[0]) else {
        println("usage: createWorkout input [output]")
        return
    }

    val output = if (args.size >= 2) Paths.get("", args[2]) else Paths.get("", "workout.zwo")

    val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByExtension("main.kts")
    val scriptText = Files.newBufferedReader(input).use { it.readText().trim() }

    // todo look into better scripting support to get
    // line numbers
    val workout = runCatching {
        scriptEngine.eval("""
            @file:CompilerOptions("-Xopt-in=kotlin.time.ExperimentalTime")
            import andrew.cash.zwift.input.*
            import andrew.cash.zwift.workout
            import kotlin.time.*
    
            $scriptText
        """.trimIndent())
    }.getOrElse {
        val scriptException = it as ScriptException
        println("""Error in config script "${input.fileName}"
            |${it.message}
        """.trimMargin())
        return
    } as Workout


    val description = workout.exercise.joinToString(separator = ", ") { it.name }
    var workouts = XmlWorkout()

    val defaultRest = SteadyState(duration = 0, power = workout.restpower.toXmlPower())

    workout.exercise.forEach { exercise ->
        workouts = when (exercise) {
            is WarmUp -> createWarmUp(workouts, exercise)
            is CoolDown -> createCoolDown(workouts, exercise)
            is VO2 -> createVO2(workouts, exercise, defaultRest)
            is FatBurner -> createFatBurner(exercise, defaultRest, workouts)
            is FTPInterval -> createFtpInterval(workouts, exercise, defaultRest)
        }
    }

    val woFile = WorkoutFile(
            author = workout.author,
            name = workout.name,
            description = description,
            workout = workouts
    )

    val xmlMapper = XmlMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    Files.newBufferedWriter(output).use {
        xmlMapper.writeValue(it, woFile)
    }
}

// used by kts scripts that are loaded
@Suppress("unused")
fun workout(name: String, author: String, restpower: Int, exercise: List<Exercise>) = Workout(
        author = author,
        name = name,
        restpower = restpower,
        exercise = exercise
)

@ExperimentalTime
private fun createFtpInterval(workouts: andrew.cash.zwift.xml.Workout, exercise: FTPInterval, defaultRest: SteadyState): andrew.cash.zwift.xml.Workout {
    return workouts.copy(steadyState = workouts.steadyState + List(exercise.sets) {
        listOf(
                SteadyState(duration = exercise.duration.toXmlSeconds(), cadence = exercise.cadence, power = exercise.power.toXmlPower()),
                defaultRest.copy(duration = exercise.rest.toXmlSeconds())
        )
    }.flatten())
}

@ExperimentalTime
private fun createFatBurner(exercise: FatBurner, defaultRest: SteadyState, workouts: andrew.cash.zwift.xml.Workout): andrew.cash.zwift.xml.Workout {
    val vo2 = defaultFatBurner.copy(power = exercise.power.toXmlPower())
    val rest = defaultRest.copy(duration = exercise.rest.toXmlSeconds())
    return workouts.copy(
            steadyState = workouts.steadyState + List(exercise.sets) {
                listOf(vo2, rest)
            }.flatten()
    )
}

@ExperimentalTime
private fun createVO2(workouts: andrew.cash.zwift.xml.Workout, exercise: VO2, defaultRest: SteadyState) = workouts.copy(
        steadyState = workouts.steadyState + List(exercise.sets) {
            listOf(
                    SteadyState(duration = exercise.duration.inSeconds.toInt(), power = exercise.power.toXmlPower()),
                    defaultRest.copy(duration = exercise.rest.toXmlSeconds())
            )
        }.flatten()
)

@ExperimentalTime
private fun createCoolDown(workouts: andrew.cash.zwift.xml.Workout, exercise: CoolDown) = workouts.copy(
        cooldown = XmlCooldown(
                Duration = exercise.duration.toXmlSeconds(),
                PowerLow = exercise.powerLow,
                PowerHigh = exercise.powerHigh
        )
)

@ExperimentalTime
private fun createWarmUp(workouts: andrew.cash.zwift.xml.Workout, warmUp: WarmUp) =
        workouts.copy(
                warmup = Warmup(
                        Duration = warmUp.duration.toXmlSeconds(),
                        PowerLow = warmUp.powerLow,
                        PowerHigh = warmUp.powerHigh
                )
        )