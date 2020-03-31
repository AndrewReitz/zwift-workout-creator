plugins {
    application
    kotlin("jvm") version "1.3.71"
    id("com.palantir.graal") version "0.6.0"
    id("com.github.ben-manes.versions") version "0.28.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("main-kts"))
    implementation(kotlin("reflect"))

    implementation("com.github.ajalt:clikt:2.6.0")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.11.0.rc1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.0.rc1")

    testImplementation(kotlin("test"))
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.10")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.10")
    testImplementation("ch.tutteli.spek:tutteli-spek-extensions:1.1.0")
    testImplementation("com.google.truth:truth:1.0.1")
}

application {
    mainClassName = "andrew.cash.zwift.AppKt"
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    kotlinOptions.jvmTarget = "1.8"
}

graal {
    mainClass("andrew.cash.zwift.AppKt")
    outputName("zwocreate")
}
