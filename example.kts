workout(
        author = "Andrew Reitz",
        name = "Friday",
        restpower = 83,
        exercise = listOf(
                WarmUp(5.minutes),
                VO2(
                        sets = 4,
                        duration = 3.minutes,
                        power = 120
                ),
                FatBurner(
                        sets = 7,
                        power = 110
                ),
                FTPInterval(
                        sets = 2,
                        duration = 20.minutes,
                        power = 96
                ),
                CoolDown(5.minutes)
        )
)
