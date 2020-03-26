plugins {
    application
    kotlin("jvm") version "1.3.71"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("main-kts"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.11.0.rc1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.0.rc1")
}

application {
    mainClassName = "andrew.cash.zwift.AppKt"
}
