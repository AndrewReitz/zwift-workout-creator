Workout(
  author = "Andrew Reitz",
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
