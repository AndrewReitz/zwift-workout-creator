package andrew.cash.zwift

import java.nio.file.Path

fun Path.readText() = toFile().readText()
fun Path.exists() = toFile().exists()