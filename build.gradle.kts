import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin

plugins {
    id("com.diffplug.spotless") version "6.8.0" apply false
}

group = "nl.jrdie.beancount.parser"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply<JavaPlugin>()
    apply<SpotlessPlugin>()
    dependencies {
        val compileOnly by configurations
        compileOnly("org.jetbrains:annotations:23.0.0")
    }
    configure<JavaPluginExtension>() {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    configure<SpotlessExtension>() {
        java {
            googleJavaFormat("1.15.0")
        }
    }
}
