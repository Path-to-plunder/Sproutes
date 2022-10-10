import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import java.io.File
import java.io.FileInputStream
import java.util.*

val compileKotlin: KotlinCompile by tasks

val kotlinVersion: String by project
val ktorVersion: String by project
val kotlinpoetVersion: String by project
val googleAutoServiceVersion: String by project

val assertKVersion: String by project

plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    mavenCentral()
}

// TODO: move version info to Kotlin file
dependencies {
    implementation(project(":annotations"))
    implementation("com.casadetasha:annotation-parser:0.2.2-alpha-4")
    implementation("com.casadetasha:kotlin-generation-dsl:0.2.1")

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")
    implementation("com.squareup:kotlinpoet:$kotlinpoetVersion")
    implementation("com.squareup:kotlinpoet-metadata:$kotlinpoetVersion")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")

    implementation("com.google.auto.service:auto-service:$googleAutoServiceVersion")
    kapt("com.google.auto.service:auto-service:$googleAutoServiceVersion")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertKVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

val prop = Properties().apply {
    load(FileInputStream(File(project.gradle.gradleUserHomeDir, "local.properties")))
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    dependsOn("javadoc")
    from(tasks.javadoc.get().destinationDir)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = prop.getProperty("ossrhUsername")
                password = prop.getProperty("ossrhPassword")
            }
        }
    }

    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            group = "com.casadetasha"
            artifactId = "sproutes-processor"
            version = "2.0.2-alpha-1"

            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name.set("Sproutes for KTOR")
                description.set("KAPT processor to manage routing boilerplate for KTOR projects. Use in conjunction" +
                        " with the sproutes library.")
                url.set("http://www.sproutes.io")

                scm {
                    connection.set("scm:git://github.com/Path-to-plunder/Sproutes")
                    developerConnection.set("scm:git:git@github.com:konk3r/Path-to-plunder/Sproutes.git")
                    url.set("https://github.com/Path-to-plunder/Sproutes")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("gabriel")
                        name.set("Gabriel Spencer")
                        email.set("gabriel@casadetasha.dev")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
