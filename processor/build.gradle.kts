import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val compileKotlin: KotlinCompile by tasks

val kotlinVersion: String by project
val ktorVersion: String by project
val kotlinpoetVersion: String by project
val googleAutoServiceVersion: String by project

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    mavenCentral()
}

// TODO: move version info to Kotlin file
dependencies {
    implementation(project(":kexp:sproute:annotations"))
    implementation(project(":kexp:annotation-parser"))

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.3.0")
    implementation("com.squareup:kotlinpoet:$kotlinpoetVersion")
    implementation("com.squareup:kotlinpoet-metadata:$kotlinpoetVersion")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")

    implementation("com.google.auto.service:auto-service:$googleAutoServiceVersion")
    kapt("com.google.auto.service:auto-service:$googleAutoServiceVersion")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.4")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}
