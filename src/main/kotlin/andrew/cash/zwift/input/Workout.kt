package andrew.cash.zwift.input

data class Workout(
        val author: String,
        val name: String,
        val restpower: Int,
        val exercise: List<Exercise>
)