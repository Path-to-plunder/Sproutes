import java.io.File
import java.io.FileInputStream
import java.util.*

plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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
            artifactId = "sproutes"
            version = "1.6.3-alpha-1"

            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name.set("Sproutes for KTOR")
                description.set("Library to manage routing boilerplate for KTOR projects. Use in conjunction with the" +
                        " sproutes-processor KAPT annotation processor")
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
